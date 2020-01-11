package com.example.samuel.pentrufacultate.models;

import java.util.HashMap;

public class VoiceCommander {
    private final HashMap<String, String> allKnownCommands = new HashMap<>();
    public final String startRecipeCommand = "START_RECIPE";
    public final String nextStepCommand = "NEXT_STEP";
    public final String previousStepCommand = "PREVIOUS_STEP";
    public final String repeatStepCommand = "REPEAT_STEP";

    public VoiceCommander() {
        init();
    }

    void init() {
        allKnownCommands.put("let's start cooking", startRecipeCommand);
        allKnownCommands.put("let's start", startRecipeCommand);

        allKnownCommands.put("next step", nextStepCommand);
        allKnownCommands.put("next please", nextStepCommand);
        allKnownCommands.put("next step please", nextStepCommand);

        allKnownCommands.put("previous step", previousStepCommand);
        allKnownCommands.put("previous please", previousStepCommand);
        allKnownCommands.put("previous step please", previousStepCommand);

        allKnownCommands.put("repeat please", repeatStepCommand);
        allKnownCommands.put("repeat the last step", repeatStepCommand);
    }

    public String getActionForVoiceCommand(String voiceCommand) {
        return allKnownCommands.get(voiceCommand);
    }
}
