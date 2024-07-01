package com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking;

import android.media.AudioFormat;
import android.media.AudioRecord;

import com.musicg.wave.WaveHeader;


import java.util.LinkedList;

import com.musicg.api.WhistleApi;

public class NewDetectorThread extends Thread{

    private RecorderThread recorder;
    private WaveHeader waveHeader;
    private WhistleApi whistleApi;
    private volatile Thread _thread;

    private LinkedList<Boolean> whistleResultList = new LinkedList<Boolean>();
    private int numWhistles;
    private int totalWhistlesDetected = 0;
    private int whistleCheckLength = 3;
    private int whistlePassScore = 3;

    public NewDetectorThread(RecorderThread recorder){
        this.recorder = recorder;
        AudioRecord audioRecord = recorder.getAudioRecord();

        int bitsPerSample = 0;
        if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT){
            bitsPerSample = 16;
        }
        else if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT){
            bitsPerSample = 8;
        }

        int channel = 0;
        // whistle detection only supports mono channel
        if (audioRecord.getChannelConfiguration() == AudioFormat.CHANNEL_CONFIGURATION_MONO){
            channel = 1;
        }

        waveHeader = new WaveHeader();
        waveHeader.setChannels(channel);
        waveHeader.setBitsPerSample(bitsPerSample);
        waveHeader.setSampleRate(audioRecord.getSampleRate());
        whistleApi = new WhistleApi(waveHeader);
    }

    private void initBuffer() {
        numWhistles = 0;
        whistleResultList.clear();

        // init the first frames
        for (int i = 0; i < whistleCheckLength; i++) {
            whistleResultList.add(false);
        }
        // end init the first frames
    }

    public void start() {
        _thread = new Thread(this);
        _thread.start();
    }

    public void stopDetection(){
        _thread = null;
    }

    public void run() {
        try {
            byte[] buffer;
            initBuffer();

            Thread thisThread = Thread.currentThread();
            while (_thread == thisThread) {
                // detect sound
                buffer = recorder.getFrameBytes();

                // audio analyst
                if (buffer != null) {
                    // sound detected
             //       MainActivity.whistleValue = numWhistles;

                    // whistle detection
                    //System.out.println("*Whistle:");
                    boolean isWhistle = whistleApi.isWhistle(buffer);
                    if (whistleResultList.getFirst()) {
                        numWhistles--;
                    }

                    whistleResultList.removeFirst();
                    whistleResultList.add(isWhistle);

                    if (isWhistle) {
                        numWhistles++;
                    }
                    //System.out.println("num:" + numWhistles);

                    if (numWhistles >= whistlePassScore) {
                        // clear buffer
                        initBuffer();
                        totalWhistlesDetected++;
                    }
                    // end whistle detection
                }
                else{
                    // no sound detected
                    if (whistleResultList.getFirst()) {
                        numWhistles--;
                    }
                    whistleResultList.removeFirst();
                    whistleResultList.add(false);

                   // MainActivity.whistleValue = numWhistles;
                }
                // end audio analyst
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTotalWhistlesDetected(){
        return totalWhistlesDetected;
    }
}