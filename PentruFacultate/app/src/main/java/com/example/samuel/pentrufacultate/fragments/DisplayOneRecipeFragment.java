package com.example.samuel.pentrufacultate.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

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
import android.widget.Toast;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.adapters.AdapterForDisplaySteps;
import com.example.samuel.pentrufacultate.managers.DataManager;
import com.example.samuel.pentrufacultate.models.RecipeModel;
import com.example.samuel.pentrufacultate.models.VoiceCommander;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;
import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

public class DisplayOneRecipeFragment extends Fragment implements RecognitionListener {

    private static final String TAG = "APP_LOG_display";
    /* Named searches allow to quickly reconfigure the decoder */
    private static final String WAITING_FOR_WAKEUP = "wakeup";
    private static final String WAITING_FOR_COMMANDS = "menu";

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "ok robo";

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final int MINIMUM_SCORE = -3700;
    private static final int MAXIMUM_SCORE = -1700;
    private static final String REIPE_JSON_EXTRA_BUNDLE_KEY = "ProcedureToDisplayJSON";

    private Context mContext;
    private DataManager dataManager;
    private RecipeModel mRecipe;
    private AdapterForDisplaySteps adapterForDisplaySteps;
    private int mCurrentStepPosition = 0;
    private ArrayList<String> mRecipeSteps = new ArrayList<>();

    private TextToSpeech mTTS;
    private edu.cmu.pocketsphinx.SpeechRecognizer recognizer = null;
    private VoiceCommander voiceCommander = new VoiceCommander();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mContext = getContext();
        mRecipe = RecipeModel.fromJson(bundle.getString(REIPE_JSON_EXTRA_BUNDLE_KEY));
        mRecipeSteps = mRecipe.getSteps();
        adapterForDisplaySteps = new AdapterForDisplaySteps(mContext, mRecipe.getSteps());
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

        dataManager = DataManager.getInstance();
        dataManager.setAdapterForShoppingListByTitle(mContext, mRecipe.getTitle());
        return inflater.inflate(R.layout.fragment_procedure_with_steps, container, false);
    }

    private void speak(String textForSpeech) {
        mTTS.speak(textForSpeech, TextToSpeech.QUEUE_FLUSH, null, "UniqueID");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.e(TAG, "onStart: speaking");
            }

            @Override
            public void onDone(String utteranceId) {

            }

            @Override
            public void onError(String utteranceId) {

            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.display_all_steps);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        Log.d(TAG, "onViewCreated: " + recyclerView);
        DividerItemDecoration itemDecor = new DividerItemDecoration(mContext, VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        recyclerView.setAdapter(adapterForDisplaySteps);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onEndOfSpeech() {

        Log.i("TAG", "onEndOfSpeech: " + recognizer.getSearchName());
        if (!recognizer.getSearchName().equals(WAITING_FOR_WAKEUP))
            switchSearch(WAITING_FOR_WAKEUP);
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
        if (text.contains(KEYPHRASE)) {
            switchSearch(WAITING_FOR_COMMANDS);
        } else {
            Log.i(TAG, "onPartialResult: " + text);
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {

        if (hypothesis != null) {
            String voiceCommand = hypothesis.getHypstr();
            if (MAXIMUM_SCORE > hypothesis.getBestScore() && hypothesis.getBestScore() > MINIMUM_SCORE) {
                parseVoiceCommand(voiceCommander.getActionForVoiceCommand(voiceCommand));
                makeText(getContext(), voiceCommand, Toast.LENGTH_SHORT).show();
            }
            if (hypothesis.getBestScore() != 0) {
                switchSearch(WAITING_FOR_WAKEUP);
            }
        }
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }

    void parseVoiceCommand(String voiceAction) {
        try {
            switch (voiceAction) {
                case "START_RECIPE":
                    Log.i(TAG, "startProcedure: " + mRecipeSteps.get(mCurrentStepPosition));
                    mCurrentStepPosition = 0;
                    speak(mRecipeSteps.get(mCurrentStepPosition));
                    break;
                case "NEXT_STEP":
                    mCurrentStepPosition++;

                    if (mCurrentStepPosition <= mRecipeSteps.size() && mCurrentStepPosition >= 0) {
                        Log.i(TAG, "nextStep: " + mRecipeSteps.get(mCurrentStepPosition));
                        speak(mRecipeSteps.get(mCurrentStepPosition));
                    } else {
                        mCurrentStepPosition--;
                        Log.e(TAG, "parseVoiceCommand: next step didn't exist ");
                    }
                    break;
                case "PREVIOUS_STEP":
                    mCurrentStepPosition--;
                    if (mCurrentStepPosition < mRecipeSteps.size() && mCurrentStepPosition >= 0) {
                        speak(mRecipeSteps.get(mCurrentStepPosition));
                    } else {
                        mCurrentStepPosition++;
                        Log.e(TAG, "parseVoiceCommand: back step didn't exist ");
                    }
                    break;
                case "REPEAT_STEP":
                    if (mCurrentStepPosition >= 0 && mCurrentStepPosition < mRecipeSteps.size()) {
                        speak(mRecipeSteps.get(mCurrentStepPosition));
                    } else {
                        Log.e(TAG, "parseVoiceCommand: repeat step didn't exist ");
                    }
                    break;
                default:
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
    }

    @Override
    public void onPause() {
        Log.i(TAG, "pause");
        super.onPause();
        if (recognizer != null) {
            recognizer.removeListener(this);
            recognizer.stop();
        }
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
                    switchSearch(WAITING_FOR_WAKEUP);
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

        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(WAITING_FOR_WAKEUP, KEYPHRASE);

        // Create grammar-based search for selection between demos
        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(WAITING_FOR_COMMANDS, menuGrammar);
        ;
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(WAITING_FOR_WAKEUP))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 3000);
    }
}

