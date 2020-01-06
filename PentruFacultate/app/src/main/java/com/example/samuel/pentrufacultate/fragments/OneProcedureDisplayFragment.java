package com.example.samuel.pentrufacultate.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.adapters.AdapterForDisplaySteps;
import com.example.samuel.pentrufacultate.managers.DataManager;
import com.example.samuel.pentrufacultate.models.EditDistanceCalculator;
import com.example.samuel.pentrufacultate.models.RecipeModel;
import com.example.samuel.pentrufacultate.models.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;
import static androidx.recyclerview.widget.RecyclerView.VERTICAL;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class OneProcedureDisplayFragment extends Fragment implements RecognitionListener {
    private static final String TAG = "APP_LOG_display";


    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String FORECAST_SEARCH = "forecast";
    private static final String DIGITS_SEARCH = "digits";
    private static final String PHONE_SEARCH = "phones";
    private static final String MENU_SEARCH = "menu";

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "ok chef";

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private SpeechRecognizer speech = null;
    private edu.cmu.pocketsphinx.SpeechRecognizer recognizer = null;
    private Intent recognizerIntent;
    private TextToSpeech mTTS;
    private String voiceInput;
    private int mCurrentStepPosition = 0;
    private AdapterForDisplaySteps adapterForDisplaySteps;
    private RecipeModel mProcedure;
    private TextView procedureName;
    private ArrayList<String> mProcedureSteps = new ArrayList<>();
    private EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();
    private AudioManager aManager;
    DataManager dataManager;
    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mContext = getContext();
        mProcedure = RecipeModel.fromJson(bundle.getString("ProcedureToDisplayJSON"));
        mProcedureSteps = mProcedure.getSteps();
        adapterForDisplaySteps = new AdapterForDisplaySteps(mContext, mProcedure.getSteps());
        // start speech recogniser
//        resetSpeechRecognizer();
        aManager = (AudioManager) getActivity().getSystemService(mContext.AUDIO_SERVICE);
        mTTS = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {

                    int result = mTTS.setLanguage(new Locale("ro", "RO"));
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        Log.i(TAG, "onInit: Succesfully  init");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        dataManager = DataManager.getInstance(mContext);
        dataManager.setAdapterForShoppingListByTitle(mContext, mProcedure.getTitle());
//        runRecognizerSetup();
        return inflater.inflate(R.layout.fragment_procedure_with_steps, container, false);
    }

    private void speak(String textForSpeech) {
        aManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);


        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        mTTS.speak(textForSpeech, TextToSpeech.QUEUE_FLUSH, map);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        aManager.setStreamMute(AudioManager.STREAM_MUSIC, true);


        mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                aManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                Log.e(TAG, "onStart: speaking");
            }

            @Override
            public void onDone(String utteranceId) {
                aManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                //something here is worng
                Handler mainHandler = new Handler(mContext.getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        startListeningSpeech();
                    }
                };
                mainHandler.post(myRunnable);

                Log.d(TAG, "onDone: speaking");
//

                Log.d(TAG, "onDone: prepared");
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
//        speech.startListening(recognizerIntent);
        procedureName = view.findViewById(R.id.procedure_name);
        procedureName.setText(mProcedure.getTitle());
        RecyclerView recyclerView = view.findViewById(R.id.display_all_steps);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        Log.d(TAG, "onViewCreated: " + recyclerView);
//        adapter.setClickListener(this);
        DividerItemDecoration itemDecor = new DividerItemDecoration(mContext, VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        recyclerView.setAdapter(adapterForDisplaySteps);


    }

    public void startListeningSpeech() {
//        speech.startListening(recognizerIntent);
    }


    private void setRecogniserIntent() {

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }


    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onEndOfSpeech() {
//        Log.i(TAG, "onEndOfSpeech");
//        speech.stopListening();

        Log.i("TAG", "onEndOfSpeech: " + recognizer.getSearchName());


        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        if (text.contains(KEYPHRASE))
            switchSearch(MENU_SEARCH);
        else
            Log.i(TAG, "onPartialResult: "+text);
//            ((TextView) findViewById(R.id.result_text)).setText(text);
    }

    @Override
    public void onResult(Hypothesis hypothesis) {

//        ((TextView) findViewById(R.id.result_text)).setText("");
        if (hypothesis != null) {
            Log.i("TESTUS", "getBestScore: " + hypothesis.getBestScore());
            Log.i("TESTUS", "getProb: " + hypothesis.getProb());
            Log.i("TESTUS", "getHypstr: " + hypothesis.getHypstr());
            String text = hypothesis.getHypstr();
            makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            switchSearch(MENU_SEARCH);
        }
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }
 void parseVoiceCommand(String voiceCommand) {
        try {


            switch (voiceCommand) {
                case "startProcedure":
                    Log.i(TAG, "startProcedure: " + mProcedureSteps.get(mCurrentStepPosition));
                    speak(mProcedureSteps.get(mCurrentStepPosition));
                    break;
                case "nextStep":
                    mCurrentStepPosition++;

                    if (mCurrentStepPosition <= mProcedureSteps.size() && mCurrentStepPosition >= 0) {
                        Log.i(TAG, "nextStep: " + mProcedureSteps.get(mCurrentStepPosition));

                        speak(mProcedureSteps.get(mCurrentStepPosition));
                    } else {
                        startListeningSpeech();
                        Log.e(TAG, "parseVoiceCommand: next step didn't exist ");
                    }
                    break;
                case "backStep":
                    mCurrentStepPosition--;
                    if (mCurrentStepPosition < mProcedureSteps.size() && mCurrentStepPosition >= 0) {
                        speak(mProcedureSteps.get(mCurrentStepPosition));
                    } else {
                        startListeningSpeech();
                        Log.e(TAG, "parseVoiceCommand: back step didn't exist ");
                    }
                    break;
                case "repeatStep":
                    if (mCurrentStepPosition >= 0 && mCurrentStepPosition < mProcedureSteps.size()) {
                        speak(mProcedureSteps.get(mCurrentStepPosition));
                    } else {
                        startListeningSpeech();
                        Log.e(TAG, "parseVoiceCommand: repeat step didn't exist ");
                    }
                    break;
                case "restartProcedure":
                    mCurrentStepPosition = 0;
                    speak(mProcedureSteps.get(mCurrentStepPosition));
                    break;
                case "goToStep":
                    mCurrentStepPosition = Integer.valueOf(stripNonDigits(voiceInput)) - 1;
                    if (mCurrentStepPosition >= 0 && mCurrentStepPosition < mProcedureSteps.size()) {
                        speak(mProcedureSteps.get(mCurrentStepPosition));
                    } else {
                        startListeningSpeech();
                        Log.e(TAG, "parseVoiceCommand: repeat step didn't exist ");
                    }
                    break;
                default:
//                    speech.startListening(recognizerIntent);
                    break;
            }
        } catch (Exception ignored) {

        }
    }



    public static String stripNonDigits(
            final CharSequence input /* inspired by seh's comment */) {
        final StringBuilder sb = new StringBuilder(
                input.length() /* also inspired by seh's comment */);
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c > 47 && c < 58) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "resume");
        super.onResume();
        runRecognizerSetup();
//        resetSpeechRecognizer();
//        speech.startListening(recognizerIntent);
    }

    @Override
    public void onPause() {
        Log.i(TAG, "pause");
        super.onPause();
        recognizer.removeListener(this);
        recognizer.stop();
//        speech.stopListening();
    }

    @Override
    public void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "stop");
        super.onStop();
//        if (speech != null) {
//            speech.destroy();
//        }
    }


    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(getContext());
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {

                    Log.i(TAG, "onPostExecute: ailed to init recognizer " + result);
//                    ((TextView) getContext().findViewById(R.id.caption_text))
//                            .setText("Failed to init recognizer " + result);
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        // Create grammar-based search for selection between demos
        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
        ;
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000
            );

//        String caption = getResources().getString(captions.get(searchName));
//        ((TextView) findViewById(R.id.caption_text)).setText(caption);
    }
}

