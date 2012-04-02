/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.shibboleth.utilities.java.support.net;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class IPAddressSupportTest {
    
    private enum AddrType {V4, V6};
    
    @Test(dataProvider = "addrData")
    public void testAddress(AddrType type, String addrString, boolean hasMask, byte[] addr) {
        switch (type) {
            case V4:
                Assert.assertTrue(IPAddressSupport.isIPv4(addr));
                Assert.assertFalse(IPAddressSupport.isIPv6(addr));
                break;
            case V6:
                Assert.assertFalse(IPAddressSupport.isIPv4(addr));
                Assert.assertTrue(IPAddressSupport.isIPv6(addr));
                break;
        }
        Assert.assertEquals(IPAddressSupport.hasMask(addr), hasMask);
        Assert.assertEquals(IPAddressSupport.addressToString(addr), addrString);
    }
    
    @DataProvider(name = "addrData")
    private Object[][] createData() {
        return new Object[][] {
                new Object[] {AddrType.V4, "192.168.4.13", false,
                        new byte[] {(byte) 192, (byte) 168, (byte) 4, (byte) 13}
                },
                
                new Object[] {AddrType.V4, "192.168.4.13/255.255.255.0", true, 
                        new byte[] {(byte) 192, (byte) 168, (byte) 4, (byte) 13, (byte) 255, (byte) 255, (byte) 255, (byte) 0}
                },
                
                new Object[] {AddrType.V4, "10.2.3.4", false,
                        new byte[] {(byte) 10, (byte) 2, (byte) 3, (byte) 4}
                },
                
                new Object[] {AddrType.V4, "10.2.3.4/255.0.0.0", true, 
                        new byte[] {(byte) 10, (byte) 2, (byte) 3, (byte) 4, (byte) 255, (byte) 0, (byte) 0, (byte) 0}
                },
                
                new Object[] {AddrType.V6, "1234:5678:90ab:cdef:ffff:aaaa:bbbb:cccc", false,
                        new byte[] {
                        (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xab, (byte) 0xcd, (byte) 0xef, 
                        (byte) 0xff, (byte) 0xff, (byte) 0xaa, (byte) 0xaa, (byte) 0xbb, (byte) 0xbb, (byte) 0xcc, (byte) 0xcc
                        }
                },
                
                new Object[] {AddrType.V6, "ff80:0:0:0:202:b3ff:fe1e:8329", false,
                        new byte[] {
                        (byte) 0xff, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, 
                        (byte) 0x02, (byte) 0x02, (byte) 0xb3, (byte) 0xff, (byte) 0xfe, (byte) 0x1e, (byte) 0x83, (byte) 0x29
                        }
                },
                        
        };
    }

}
