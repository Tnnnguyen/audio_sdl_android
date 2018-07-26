package com.smartdevicelink.test.api.audio;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.smartdevicelink.api.audio.AudioDecoderCompat;
import com.smartdevicelink.api.audio.AudioDecoderListener;
import com.smartdevicelink.api.audio.AudioStreamManager;
import com.smartdevicelink.api.audio.BaseAudioDecoder;
import com.smartdevicelink.api.audio.SampleBuffer;
import com.smartdevicelink.api.audio.SampleType;
import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.protocol.enums.SessionType;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.interfaces.ISdl;
import com.smartdevicelink.proxy.interfaces.ISdlServiceListener;
import com.smartdevicelink.proxy.rpc.enums.BitsPerSample;
import com.smartdevicelink.proxy.rpc.enums.SamplingRate;
import com.smartdevicelink.proxy.rpc.listeners.OnRPCNotificationListener;
import com.smartdevicelink.streaming.video.VideoStreamingParameters;
import com.smartdevicelink.transport.SdlRouterService;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteOrder;

public class AudioStreamManagerTest extends TestCase {

    Context mContext;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mContext = InstrumentationRegistry.getContext();
    }

    ISdl sdlInterface = new ISdl() {
        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public boolean isConnected() {
            return false;
        }

        @Override
        public void addServiceListener(SessionType serviceType, ISdlServiceListener sdlServiceListener) {

        }

        @Override
        public void removeServiceListener(SessionType serviceType, ISdlServiceListener sdlServiceListener) {

        }

        @Override
        public void startVideoService(VideoStreamingParameters parameters, boolean encrypted) {

        }

        @Override
        public void stopVideoService() {

        }

        @Override
        public void startAudioService(boolean encrypted) {

        }

        @Override
        public void stopAudioService() {

        }

        @Override
        public void sendRPCRequest(RPCRequest message) {

        }

        @Override
        public void addOnRPCNotificationListener(FunctionID notificationId, OnRPCNotificationListener listener) {

        }

        @Override
        public boolean removeOnRPCNotificationListener(FunctionID notificationId, OnRPCNotificationListener listener) {
            return false;
        }
    };

    public void testCreatingAudioStreamManager() {
        AudioStreamManager manager = new AudioStreamManager(sdlInterface, SamplingRate._16KHZ, BitsPerSample._16_BIT);
    }

    public void testStartAudioStreamManager() {
        AudioStreamManager manager = new AudioStreamManager(sdlInterface, SamplingRate._16KHZ, BitsPerSample._16_BIT);
        manager.startAudioService(false);


    } // start audio method

    public void testSampleAtTargetTime() {

        Constructor<AudioDecoderCompat> constructor = (Constructor<AudioDecoderCompat>) AudioDecoderCompat.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        AudioDecoderCompat mockDecoder = null;
        try {
            mockDecoder = constructor.newInstance(getSampleFile(), 16000, SampleType.SIGNED_16_BIT, getListener());
        } catch (InstantiationException e) {
            e.printStackTrace();
            fail();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            fail();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            fail();
        }

        Method sampleAtTargetMethod = null;
        try {
            sampleAtTargetMethod = BaseAudioDecoder.class.getDeclaredMethod("sampleAtTargetTime",
                      double.class, SampleBuffer.class, long.class, long.class, long.class);
            sampleAtTargetMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            fail();
        }

        // supply range of values here to test
        SampleBuffer sample = SampleBuffer.allocate(1, SampleType.SIGNED_16_BIT, ByteOrder.LITTLE_ENDIAN, 1);
        Double result = null;
        try {
            // supply range of values here to test
            result = (Double) sampleAtTargetMethod.invoke(mockDecoder, 1.0, sample, 1, 2, 3);
            // depending on supplied values, assert result's value

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            fail();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            fail();
        }
    }


    private AudioDecoderListener getListener() {
        AudioDecoderListener listener = new AudioDecoderListener() {
            @Override
            public void onAudioDataAvailable(SampleBuffer buffer) {
                //inspect buffer here
            }

            @Override
            public void onDecoderFinish() {
            }

            @Override
            public void onDecoderError(Exception e) {
            }
        };
        return listener;
    }


    private File getSampleFile() {
        File file = new File(mContext.getCacheDir()+"/loaded.mp3");
        try {
            InputStream is = mContext.getAssets().open("warning.mp3");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }


}
