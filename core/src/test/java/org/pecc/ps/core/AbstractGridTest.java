package org.pecc.ps.core;

import org.epcc.ps.core.entity.environment.Grid;
import org.epcc.ps.core.entity.environment.GridFactory;
import org.epcc.ps.core.entity.environment.Terrain;

/**
 * @author shaohan.yin
 * Created on 16/10/2017
 */
public class AbstractGridTest extends AbstractTest {
    protected Grid createGridWithWater() {
        return GridFactory.create(Terrain.WATER);
    }

    protected Grid createGridWithLand() {
        return GridFactory.create(Terrain.LAND);
    }
}