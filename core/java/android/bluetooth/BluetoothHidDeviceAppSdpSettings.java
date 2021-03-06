/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents the Service Discovery Protocol (SDP) settings for a Bluetooth
 * HID Device application.
 *
 * The BluetoothHidDevice framework adds the SDP record during app
 * registration, so that the Android device can be discovered as a Bluetooth
 * HID Device.
 *
 * {@see BluetoothHidDevice}
 *
 * {@hide}
 */
public final class BluetoothHidDeviceAppSdpSettings implements Parcelable {

    public final String name;
    public final String description;
    public final String provider;
    public final byte subclass;
    public final byte[] descriptors;

    public BluetoothHidDeviceAppSdpSettings(String name, String description, String provider,
            byte subclass, byte[] descriptors) {
        this.name = name;
        this.description = description;
        this.provider = provider;
        this.subclass = subclass;
        this.descriptors = descriptors.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BluetoothHidDeviceAppSdpSettings) {
            BluetoothHidDeviceAppSdpSettings sdp = (BluetoothHidDeviceAppSdpSettings) o;
            return false;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<BluetoothHidDeviceAppSdpSettings> CREATOR =
            new Parcelable.Creator<BluetoothHidDeviceAppSdpSettings>() {

                @Override
                public BluetoothHidDeviceAppSdpSettings createFromParcel(Parcel in) {

                    return new BluetoothHidDeviceAppSdpSettings(
                            in.readString(),
                            in.readString(),
                            in.readString(),
                            in.readByte(),
                            in.createByteArray());
                }

                @Override
                public BluetoothHidDeviceAppSdpSettings[] newArray(int size) {
                    return new BluetoothHidDeviceAppSdpSettings[size];
                }
            };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(description);
        out.writeString(provider);
        out.writeByte(subclass);
        out.writeByteArray(descriptors);
    }
}
