/*
 * Copyright 2016 MovingBlocks
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
package org.terasology.inferno.generator.providers;

import org.terasology.inferno.generator.facets.InfernoCeilingHeightFacet;
import org.terasology.inferno.generator.facets.InfernoSurfaceHeightFacet;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2f;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.utilities.procedural.SubSampledNoise;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;

@Produces(InfernoCeilingHeightFacet.class)
@Requires(@Facet(InfernoSurfaceHeightFacet.class))
public class InfernoCeilingProvider implements FacetProvider {
    private Noise surfaceNoise;
    private int infernoHeight;

    public InfernoCeilingProvider(int height) {
        this.infernoHeight = height;
    }

    @Override
    public void setSeed(long seed) {
        surfaceNoise = new SubSampledNoise(new SimplexNoise(seed + 1), new Vector2f(0.003f, 0.003f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(InfernoCeilingHeightFacet.class);
        InfernoCeilingHeightFacet ceilingHeightFacet = new InfernoCeilingHeightFacet(region.getRegion(), border);
        InfernoSurfaceHeightFacet surfaceHeightFacet = region.getRegionFacet(InfernoSurfaceHeightFacet.class);
        int baseSurfaceHeight = surfaceHeightFacet.getBaseSurfaceHeight();

        Rect2i processRegion = ceilingHeightFacet.getWorldRegion();
        for (BaseVector2i position : processRegion.contents()) {
            ceilingHeightFacet.setWorld(position, surfaceNoise.noise(position.x(), position.y()) * 20 - baseSurfaceHeight + infernoHeight);
        }

        region.setRegionFacet(InfernoCeilingHeightFacet.class, ceilingHeightFacet);
    }
}
