/*
 * Copyright 2014 Jason Burgess
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atmosphere.cpr;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;

import static org.mockito.Mockito.mock;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

public class AtmosphereResourceFactoryTest {

    private AtmosphereFramework framework;

    @BeforeMethod
    public void create() throws Throwable {
        framework = new AtmosphereFramework();
        framework.setAsyncSupport(mock(AsyncSupport.class));
        framework.init(new ServletConfig() {
            @Override
            public String getServletName() {
                return "void";
            }

            @Override
            public ServletContext getServletContext() {
                return mock(ServletContext.class);
            }

            @Override
            public String getInitParameter(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return null;
            }
        });
    }

    @Test
    public void createTest() {
        AtmosphereResource r = framework.getAtmosphereConfig().resourcesFactory().create(framework.getAtmosphereConfig(), mock(Broadcaster.class), AtmosphereResponse.newInstance().request(AtmosphereRequest.newInstance()),
                mock(AsyncSupport.class), mock(AtmosphereHandler.class), AtmosphereResource.TRANSPORT.WEBSOCKET);
        assertNotNull(r);
    }

    @Test
    public void findTest() {
        Broadcaster b1 = BroadcasterFactory.getDefault().get("b1");
        Broadcaster b2 = BroadcasterFactory.getDefault().get("b2");
        AtmosphereResource r = framework.getAtmosphereConfig().resourcesFactory().create(framework.getAtmosphereConfig(), b1, AtmosphereResponse.newInstance().request(AtmosphereRequest.newInstance()),
                mock(AsyncSupport.class), mock(AtmosphereHandler.class), AtmosphereResource.TRANSPORT.WEBSOCKET);
        assertNotNull(r);

        b2.addAtmosphereResource(r.suspend());

        assertNotNull(framework.getAtmosphereConfig().resourcesFactory().find(r.uuid()));

    }

    @Test
    public void notFoundTest() {
        for (int i = 0; i < 10; i++) {
            BroadcasterFactory.getDefault().get(String.valueOf(i));
        }
        AtmosphereResource r = framework.getAtmosphereConfig().resourcesFactory().create(framework.getAtmosphereConfig(), BroadcasterFactory.getDefault().lookup("1"), AtmosphereResponse.newInstance().request(AtmosphereRequest.newInstance()),
                mock(AsyncSupport.class), mock(AtmosphereHandler.class));
        assertNotNull(r);
        assertNull(framework.getAtmosphereConfig().resourcesFactory().find(r.uuid()));
    }

    @Test
    public void deleteTest() {
        for (int i = 0; i < 10; i++) {
            BroadcasterFactory.getDefault().get(String.valueOf(i));
        }
        Broadcaster b2 = BroadcasterFactory.getDefault().get("b2");
        AtmosphereResource r = framework.getAtmosphereConfig().resourcesFactory().create(framework.config, BroadcasterFactory.getDefault().lookup("1"), AtmosphereResponse.newInstance().request(AtmosphereRequest.newInstance()),
                mock(AsyncSupport.class), mock(AtmosphereHandler.class), AtmosphereResource.TRANSPORT.WEBSOCKET);
        assertNotNull(r);
        b2.addAtmosphereResource(r.suspend());

        assertNotNull(framework.getAtmosphereConfig().resourcesFactory().find(r.uuid()));
        assertEquals(framework.getAtmosphereConfig().resourcesFactory().remove(r.uuid()), r);
        assertNull(framework.getAtmosphereConfig().resourcesFactory().find(r.uuid()));
    }

    @Test
    public void broadcastersTest() {
        Broadcaster b1 = BroadcasterFactory.getDefault().get("b1");
        Broadcaster b2 = BroadcasterFactory.getDefault().get("b2");
        AtmosphereResource r = framework.getAtmosphereConfig().resourcesFactory().create(framework.getAtmosphereConfig(), b1, AtmosphereResponse.newInstance().request(AtmosphereRequest.newInstance()),
                mock(AsyncSupport.class), mock(AtmosphereHandler.class), AtmosphereResource.TRANSPORT.WEBSOCKET);
        r.suspend();
        assertNotNull(r);

        b2.addAtmosphereResource(r);

        assertNotNull(framework.getAtmosphereConfig().resourcesFactory().find(r.uuid()));
        assertEquals(2, framework.getAtmosphereConfig().resourcesFactory().broadcasters(r.uuid()).size());

    }
}
