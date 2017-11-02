package org.pecc.ps.core.algorithm;

import org.epcc.ps.core.algorithm.CoreAlgorithm;
import org.epcc.ps.core.entity.creature.Species;
import org.epcc.ps.core.entity.environment.Grid;
import org.epcc.ps.core.entity.environment.Terrain;
import org.epcc.ps.core.util.GridUtil;
import org.junit.Assert;
import org.junit.Test;
import org.pecc.ps.core.AbstractGridTest;

/**
 * @author jiahao.cao
 * Created on 10/13/2017
 */
public class AlgorithmTest extends AbstractGridTest {
    private CoreAlgorithm coreAlgorithm = CoreAlgorithm.DEFAULT;

    @Test
    public void testSingleIterationAlgorithm() {
        Grid grids[][] = {
                {createGridWithLand(), createGridWithLand(), createGridWithLand()},
                {createGridWithLand(), createGridWithWater(), createGridWithLand()},
                {createGridWithLand(), createGridWithLand(), createGridWithWater()}
        };
        Grid[][] gridsWithHalo = GridUtil.generateGridWithHaloBoundary(3, 3, grids);

        int hareNum = 9;
        int pumaNum = 1;
        for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids[0].length; j++) {
                switch (grids[i][j].getTerrain()) {
                    case LAND:
                        initGridDensity(grids[i][j], hareNum, pumaNum);
                        --hareNum;
                        ++pumaNum;
                        break;
                    case WATER:
                        initGridDensity(grids[i][j], 0, 0);
                        break;
                    default:
                        initGridDensity(grids[i][j], 0, 0);
                }
            }
        }

        for (int i = 0; i < grids.length; ++i) {
            for (int j = 0; j < grids[0].length; ++j) {
                grids[i][j].setLandNeighborCnt(GridUtil.getNeighborCntWithType(
                        i + 1, j + 1,
                        gridsWithHalo, Terrain.LAND
                ));
            }
        }

        double newHareDensity, newPumaDensity;
        gridsWithHalo = GridUtil.generateGridWithHaloBoundary(3, 3, grids);
        for (int i = 0; i < grids.length; ++i) {
            for (int j = 0; j < grids[0].length; ++j) {
                newHareDensity = coreAlgorithm.getHaresNum(
                        grids[i][j].getTerrain(),
                        gridsWithHalo[i + 1][j + 1].getDensity(Species.HARE),
                        gridsWithHalo[i + 1][j].getDensity(Species.HARE),
                        gridsWithHalo[i + 1][j + 2].getDensity(Species.HARE),
                        gridsWithHalo[i][j + 1].getDensity(Species.HARE),
                        gridsWithHalo[i + 2][j + 1].getDensity(Species.HARE),
                        Species.HARE.getBirthRate(),
                        Species.PUMA.getPredationRate(),
                        gridsWithHalo[i + 1][j + 1].getDensity(Species.PUMA),
                        Species.HARE.getDiffusionRate(),
                        0.4,
                        grids[i][j].getLandNeighborCnt());

                newPumaDensity = coreAlgorithm.getPumaNum(
                        grids[i][j].getTerrain(),
                        gridsWithHalo[i + 1][j + 1].getDensity(Species.PUMA),
                        gridsWithHalo[i + 1][j].getDensity(Species.PUMA),
                        gridsWithHalo[i + 1][j + 2].getDensity(Species.PUMA),
                        gridsWithHalo[i][j + 1].getDensity(Species.PUMA),
                        gridsWithHalo[i + 2][j + 1].getDensity(Species.PUMA),
                        Species.PUMA.getBirthRate(),
                        gridsWithHalo[i + 1][j + 1].getDensity(Species.HARE),
                        Species.PUMA.getMortalityRate(),
                        Species.PUMA.getDiffusionRate(),
                        0.4,
                        grids[i][j].getLandNeighborCnt());

                grids[i][j].updateDensity(Species.HARE, newHareDensity);
                grids[i][j].updateDensity(Species.PUMA, newPumaDensity);
            }
        }

        Assert.assertEquals(8.824, grids[0][0].getDensity(Species.HARE), 0);
        Assert.assertEquals(1.368, grids[0][0].getDensity(Species.PUMA), 0);
    }

}
