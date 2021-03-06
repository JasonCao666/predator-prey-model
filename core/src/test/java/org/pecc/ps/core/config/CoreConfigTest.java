/*
 * Copyright (C) 2017 the predator-prey-model authors.
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
 *
 */

package org.pecc.ps.core.config;

import org.epcc.ps.core.config.CoreConfig;
import org.junit.Assert;
import org.junit.Test;
import org.pecc.ps.core.AbstractTest;

/**
 * @author shaohan.yin
 * Created on 11/10/2017
 */
public class CoreConfigTest extends AbstractTest {
    CoreConfig config = CoreConfig.DEFAULT;

    @Test
    public void testCoreConfig() {
        Assert.assertEquals(0.4, config.getLandscapeEvolutionTimeStep(), 0);
        Assert.assertEquals(0, config.getLandscapeEvolutionTimeStart(), 0);
        Assert.assertEquals(500, config.getLandscapeEvolutionTimeEnd(), 0);
        Assert.assertEquals(5, config.getLandscapeEvolutionTimeOutput());
        Assert.assertEquals(2000, config.getLandscapeLengthLimit());
        Assert.assertEquals(2000, config.getLandscapeWidthLimit());
        Assert.assertEquals(0.08, config.getHareBirthRate(), 0);
        Assert.assertEquals(0.2, config.getHareDiffusionRate(), 0);
        Assert.assertEquals(0.02, config.getPumaBirthRate(), 0);
        Assert.assertEquals(0.2, config.getPumaDiffusionRate(), 0);
        Assert.assertEquals(0.06, config.getPumaMortalityRate(), 0);
        Assert.assertEquals(0.04, config.getPumaPredationRate(), 0);
    }
}
