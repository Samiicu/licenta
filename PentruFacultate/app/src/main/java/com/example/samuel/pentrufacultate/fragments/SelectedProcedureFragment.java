package com.example.samuel.pentrufacultate.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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
import com.example.samuel.pentrufacultate.adapters.AdapterForDisplayProcedure;
import com.example.samuel.pentrufacultate.adapters.AdapterForDisplaySteps;
import com.example.samuel.pentrufacultate.models.Clasificator;
import com.example.samuel.pentrufacultate.models.ProcedureModel;
import com.example.samuel.pentrufacultate.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class SelectedProcedureFragment extends Fragment implements
        RecognitionListener {
    private static final String TAG = "APP_LOG_display";
    AdapterForDisplaySteps adapterForDisplaySteps;
    ProcedureModel mProcedure;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    TextView procedureName;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    Clasificator clasificator = new Clasificator();
    private String uidCurrentUser;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mProcedure = ProcedureModel.fromJson(bundle.getString("ProcedureToDisplayJSON"));
        uidCurrentUser = bundle.getString("userUid");
        adapterForDisplaySteps = new AdapterForDisplaySteps(getContext(), mProcedure.getSteps());
        // start speech recogniser
        resetSpeechRecognizer();

        // check for permission

        return inflater.inflate(R.layout.fragment_procedure_with_steps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        AudioManager amanager = (AudioManager) getActivity().getSystemService(getContext().AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
        amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        amanager.setStreamMute(AudioManager.STREAM_RING, true);
        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        setRecogniserIntent();

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
        if (!voiceCommand.equals("")) {
            parseVoiceCommand(voiceCommand);
            Log.i(TAG, "onResults: " + clasificator.getMostProbableAction(voiceCommand));
        } else {
            Log.i(TAG, "onResults: FAIL");
        }
        speech.startListening(recognizerIntent);
    }

    private void parseVoiceCommand(String voiceCommand) {
        switch (voiceCommand) {
            case "nextStep":

                //TODO going to the next step
                break;
            case "backStep":
                //TODO going to the back step
                break;
            case "repeatStep":
                //TODO going to the repeat step
                break;
            case "restartProcedure":
                //TODO going to the restart procedure
                break;

        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i(TAG, "onPartialResults");

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
    public void onStop() {
        Log.i(TAG, "stop");
        super.onStop();
        if (speech != null) {
            speech.destroy();
        }
    }
}
