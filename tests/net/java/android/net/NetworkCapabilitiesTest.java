/*
 * Copyright (C) 2017 The Android Open Source Project
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

package android.net;

import static android.net.NetworkCapabilities.LINK_BANDWIDTH_UNSPECIFIED;
import static android.net.NetworkCapabilities.NET_CAPABILITY_CBS;
import static android.net.NetworkCapabilities.NET_CAPABILITY_EIMS;
import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;
import static android.net.NetworkCapabilities.NET_CAPABILITY_NOT_METERED;
import static android.net.NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED;
import static android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED;
import static android.net.NetworkCapabilities.RESTRICTED_CAPABILITIES;
import static android.net.NetworkCapabilities.TRANSPORT_CELLULAR;
import static android.net.NetworkCapabilities.TRANSPORT_WIFI;
import static android.net.NetworkCapabilities.UNRESTRICTED_CAPABILITIES;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class NetworkCapabilitiesTest {
    @Test
    public void testMaybeMarkCapabilitiesRestricted() {
        // verify EIMS is restricted
        assertEquals((1 << NET_CAPABILITY_EIMS) & RESTRICTED_CAPABILITIES,
                (1 << NET_CAPABILITY_EIMS));

        // verify CBS is also restricted
        assertEquals((1 << NET_CAPABILITY_CBS) & RESTRICTED_CAPABILITIES,
                (1 << NET_CAPABILITY_CBS));

        // verify default is not restricted
        assertEquals((1 << NET_CAPABILITY_INTERNET) & RESTRICTED_CAPABILITIES, 0);

        // just to see
        assertEquals(RESTRICTED_CAPABILITIES & UNRESTRICTED_CAPABILITIES, 0);

        // check that internet does not get restricted
        NetworkCapabilities netCap = new NetworkCapabilities();
        netCap.addCapability(NET_CAPABILITY_INTERNET);
        netCap.maybeMarkCapabilitiesRestricted();
        assertTrue(netCap.hasCapability(NET_CAPABILITY_NOT_RESTRICTED));

        // metered-ness shouldn't matter
        netCap = new NetworkCapabilities();
        netCap.addCapability(NET_CAPABILITY_INTERNET);
        netCap.addCapability(NET_CAPABILITY_NOT_METERED);
        netCap.maybeMarkCapabilitiesRestricted();
        assertTrue(netCap.hasCapability(NET_CAPABILITY_NOT_RESTRICTED));
        netCap = new NetworkCapabilities();
        netCap.addCapability(NET_CAPABILITY_INTERNET);
        netCap.removeCapability(NET_CAPABILITY_NOT_METERED);
        netCap.maybeMarkCapabilitiesRestricted();
        assertTrue(netCap.hasCapability(NET_CAPABILITY_NOT_RESTRICTED));

        // add EIMS - bundled with unrestricted means it's unrestricted
        netCap = new NetworkCapabilities();
        netCap.addCapability(NET_CAPABILITY_INTERNET);
        netCap.addCapability(NET_CAPABILITY_EIMS);
        netCap.addCapability(NET_CAPABILITY_NOT_METERED);
        netCap.maybeMarkCapabilitiesRestricted();
        assertTrue(netCap.hasCapability(NET_CAPABILITY_NOT_RESTRICTED));
        netCap = new NetworkCapabilities();
        netCap.addCapability(NET_CAPABILITY_INTERNET);
        netCap.addCapability(NET_CAPABILITY_EIMS);
        netCap.removeCapability(NET_CAPABILITY_NOT_METERED);
        netCap.maybeMarkCapabilitiesRestricted();
        assertTrue(netCap.hasCapability(NET_CAPABILITY_NOT_RESTRICTED));

        // just a restricted cap should be restricted regardless of meteredness
        netCap = new NetworkCapabilities();
        netCap.addCapability(NET_CAPABILITY_EIMS);
        netCap.addCapability(NET_CAPABILITY_NOT_METERED);
        netCap.maybeMarkCapabilitiesRestricted();
        assertFalse(netCap.hasCapability(NET_CAPABILITY_NOT_RESTRICTED));
        netCap = new NetworkCapabilities();
        netCap.addCapability(NET_CAPABILITY_EIMS);
        netCap.removeCapability(NET_CAPABILITY_NOT_METERED);
        netCap.maybeMarkCapabilitiesRestricted();
        assertFalse(netCap.hasCapability(NET_CAPABILITY_NOT_RESTRICTED));

        // try 2 restricted caps
        netCap = new NetworkCapabilities();
        netCap.addCapability(NET_CAPABILITY_CBS);
        netCap.addCapability(NET_CAPABILITY_EIMS);
        netCap.addCapability(NET_CAPABILITY_NOT_METERED);
        netCap.maybeMarkCapabilitiesRestricted();
        assertFalse(netCap.hasCapability(NET_CAPABILITY_NOT_RESTRICTED));
        netCap = new NetworkCapabilities();
        netCap.addCapability(NET_CAPABILITY_CBS);
        netCap.addCapability(NET_CAPABILITY_EIMS);
        netCap.removeCapability(NET_CAPABILITY_NOT_METERED);
        netCap.maybeMarkCapabilitiesRestricted();
        assertFalse(netCap.hasCapability(NET_CAPABILITY_NOT_RESTRICTED));
    }

    @Test
    public void testDescribeImmutableDifferences() {
        NetworkCapabilities nc1;
        NetworkCapabilities nc2;

        // Transports changing
        nc1 = new NetworkCapabilities().addTransportType(TRANSPORT_CELLULAR);
        nc2 = new NetworkCapabilities().addTransportType(TRANSPORT_WIFI);
        assertNotEquals("", nc1.describeImmutableDifferences(nc2));
        assertEquals("", nc1.describeImmutableDifferences(nc1));

        // Mutable capability changing
        nc1 = new NetworkCapabilities().addCapability(NET_CAPABILITY_VALIDATED);
        nc2 = new NetworkCapabilities();
        assertEquals("", nc1.describeImmutableDifferences(nc2));
        assertEquals("", nc1.describeImmutableDifferences(nc1));

        // NOT_METERED changing (http://b/63326103)
        nc1 = new NetworkCapabilities()
                .addCapability(NET_CAPABILITY_NOT_METERED)
                .addCapability(NET_CAPABILITY_INTERNET);
        nc2 = new NetworkCapabilities().addCapability(NET_CAPABILITY_INTERNET);
        assertEquals("", nc1.describeImmutableDifferences(nc2));
        assertEquals("", nc1.describeImmutableDifferences(nc1));

        // Immutable capability changing
        nc1 = new NetworkCapabilities()
                .addCapability(NET_CAPABILITY_INTERNET)
                .removeCapability(NET_CAPABILITY_NOT_RESTRICTED);
        nc2 = new NetworkCapabilities().addCapability(NET_CAPABILITY_INTERNET);
        assertNotEquals("", nc1.describeImmutableDifferences(nc2));
        assertEquals("", nc1.describeImmutableDifferences(nc1));

        // Specifier changing
        nc1 = new NetworkCapabilities().addTransportType(TRANSPORT_WIFI);
        nc2 = new NetworkCapabilities()
                .addTransportType(TRANSPORT_WIFI)
                .setNetworkSpecifier(new StringNetworkSpecifier("specs"));
        assertNotEquals("", nc1.describeImmutableDifferences(nc2));
        assertEquals("", nc1.describeImmutableDifferences(nc1));
    }

    @Test
    public void testLinkBandwidthUtils() {
        assertEquals(LINK_BANDWIDTH_UNSPECIFIED, NetworkCapabilities
                .minBandwidth(LINK_BANDWIDTH_UNSPECIFIED, LINK_BANDWIDTH_UNSPECIFIED));
        assertEquals(10, NetworkCapabilities
                .minBandwidth(LINK_BANDWIDTH_UNSPECIFIED, 10));
        assertEquals(10, NetworkCapabilities
                .minBandwidth(10, LINK_BANDWIDTH_UNSPECIFIED));
        assertEquals(10, NetworkCapabilities
                .minBandwidth(10, 20));

        assertEquals(LINK_BANDWIDTH_UNSPECIFIED, NetworkCapabilities
                .maxBandwidth(LINK_BANDWIDTH_UNSPECIFIED, LINK_BANDWIDTH_UNSPECIFIED));
        assertEquals(10, NetworkCapabilities
                .maxBandwidth(LINK_BANDWIDTH_UNSPECIFIED, 10));
        assertEquals(10, NetworkCapabilities
                .maxBandwidth(10, LINK_BANDWIDTH_UNSPECIFIED));
        assertEquals(20, NetworkCapabilities
                .maxBandwidth(10, 20));
    }
}
