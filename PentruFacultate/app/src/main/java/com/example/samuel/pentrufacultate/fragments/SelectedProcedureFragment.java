package com.example.samuel.pentrufacultate.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.adapters.AdapterForDisplaySteps;
import com.example.samuel.pentrufacultate.models.Clasificator;
import com.example.samuel.pentrufacultate.models.ProcedureModel;
import com.example.samuel.pentrufacultate.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class SelectedProcedureFragment extends Fragment implements RecognitionListener {
    private static final String TAG = "APP_LOG_display";
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private TextToSpeech mTTS;
    private String uidCurrentUser;
    private User currentUser;
    private String voiceInput;
    private int mCurrentStepPosition = 0;
    AdapterForDisplaySteps adapterForDisplaySteps;
    ProcedureModel mProcedure;
    TextView procedureName;
    ArrayList<String> mProcedureSteps = new ArrayList<>();
    Clasificator clasificator = new Clasificator();
    AudioManager amanager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mProcedure = ProcedureModel.fromJson(bundle.getString("ProcedureToDisplayJSON"));
        mProcedureSteps = mProcedure.getSteps();
        uidCurrentUser = bundle.getString("userUid");
        adapterForDisplaySteps = new AdapterForDisplaySteps(getContext(), mProcedure.getSteps());
        // start speech recogniser
        resetSpeechRecognizer();
        amanager = (AudioManager) getActivity().getSystemService(getContext().AUDIO_SERVICE);
        mTTS = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {

                    int result = mTTS.setLanguage(Locale.ENGLISH);
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

        return inflater.inflate(R.layout.fragment_procedure_with_steps, container, false);
    }

    private void speak(String textForSpeech) {
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        mTTS.speak(textForSpeech, TextToSpeech.QUEUE_FLUSH, map);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);


        setRecogniserIntent();
        mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                Log.e(TAG, "onStart: speaking");
            }

            @Override
            public void onDone(String utteranceId) {
                amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                //something here is worng
                Handler mainHandler = new Handler(getContext().getMainLooper());

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
        speech.startListening(recognizerIntent);
        procedureName = view.findViewById(R.id.procedure_name);
        procedureName.setText(mProcedure.getName());
        RecyclerView recyclerView = view.findViewById(R.id.display_all_steps);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d(TAG, "onViewCreated: " + recyclerView);
//        adapter.setClickListener(this);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        recyclerView.setAdapter(adapterForDisplaySteps);


    }

    public void startListeningSpeech() {
        speech.startListening(recognizerIntent);
    }


    private void setRecogniserIntent() {

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    private void resetSpeechRecognizer() {

        if (speech != null)
            speech.destroy();
        speech = SpeechRecognizer.createSpeechRecognizer(getContext());
        Log.i(TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(getContext()));
        if (SpeechRecognizer.isRecognitionAvailable(getContext()))
            speech.setRecognitionListener(this);
        else {
            Log.i(TAG, "resetSpeechRecognizer: returned");
            return;
        }
    }


    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i(TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "onEndOfSpeech");
        speech.stopListening();
    }

    @Override
    public void onError(int error) {
        String errorMessage = getErrorText(error);
        Log.i(TAG, "FAILED " + errorMessage);


        // rest voice recogniser
        resetSpeechRecognizer();
        speech.startListening(recognizerIntent);
    }


    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//        String text = "";
//        for (String result : matches)
//            text += result + "\n";
        String voiceCommand = clasificator.prepareComandForED(matches.get(0));
        Log.i(TAG, "onResults: RESULT: " + matches.get(0));
        voiceInput=matches.get(0);
        if (!voiceCommand.equals("") && voiceCommand != null) {

            final String mostProbableAction = clasificator.getMostProbableAction(voiceCommand);
            if (mostProbableAction != null) {
                parseVoiceCommand(mostProbableAction);
                Log.e(TAG, "onResults: afterParseCommand");
//                parseVoiceCommand(mostProbableAction);
            } else {
                speech.startListening(recognizerIntent);
            }
            Log.i(TAG, "onResults: " + clasificator.getMostProbableAction(voiceCommand));
        } else {
            Log.i(TAG, "onResults: FAIL");
        }

    }


    private void parseVoiceCommand(String voiceCommand) {
        try {


            switch (voiceCommand) {
                case "startProcedure":
                    Log.i(TAG, "startProcedure: " + mProcedureSteps.get(mCurrentStepPosition));
                    speak(mProcedureSteps.get(mCurrentStepPosition));
                    break;
                case "nextStep":
                    mCurrentStepPosition++;

                        if (mCurrentStepPosition <=mProcedureSteps.size() && mCurrentStepPosition >= 0) {
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
                   mCurrentStepPosition=Integer.valueOf(stripNonDigits(voiceInput))-1 ;
                    if (mCurrentStepPosition >= 0 && mCurrentStepPosition < mProcedureSteps.size()) {
                        speak(mProcedureSteps.get(mCurrentStepPosition));
                    } else {
                        startListeningSpeech();
                        Log.e(TAG, "parseVoiceCommand: repeat step didn't exist ");
                    }
                    break;
                default:
                    speech.startListening(recognizerIntent);
                    break;
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i(TAG, "onPartialResults");

    }
    public static String stripNonDigits(
            final CharSequence input /* inspired by seh's comment */){
        final StringBuilder sb = new StringBuilder(
                input.length() /* also inspired by seh's comment */);
        for(int i = 0; i < input.length(); i++){
            final char c = input.charAt(i);
            if(c > 47 && c < 58){
                sb.append(c);
            }
        }
        return sb.toString();
    }
    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.i(TAG, "onEvent");
    }

    @Override
    public void onResume() {
        Log.i(TAG, "resume");
        super.onResume();
        resetSpeechRecognizer();
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onPause() {
        Log.i(TAG, "pause");
        super.onPause();
        speech.stopListening();
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
        if (speech != null) {
            speech.destroy();
        }
    }
}

