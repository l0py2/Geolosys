package com.oitsjustjose.geolosys.world;

import com.oitsjustjose.geolosys.Geolosys;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.ArrayList;
import java.util.Random;

/**
 * A modified version of:
 * https://github.com/BluSunrize/ImmersiveEngineering/blob/master/src/main/java/blusunrize/immersiveengineering/common/world/IEWorldGen.java
 * Original Source & Credit: BluSunrize
 **/

public class OreGenerator implements IWorldGenerator
{
    public static class OreGen
    {
        WorldGenOrePluton pluton;
        IBlockState state;
        int minY;
        int maxY;
        int weight;


        public OreGen(IBlockState state, int maxVeinSize, Block replaceTarget, int minY, int maxY, int weight)
        {
            this.pluton = new WorldGenOrePluton(state, maxVeinSize, BlockMatcher.forBlock(replaceTarget));
            this.state = state;
            this.minY = minY;
            this.maxY = maxY;
            this.weight = weight;
        }

        public void generate(World world, Random rand, int x, int z)
        {
            if (!Geolosys.chunkOreGen.canGenerateInChunk(new ChunkPos(x / 16, z / 16)))
                return;
            BlockPos pos;
            if (rand.nextInt(100) < weight)
            {
                pos = new BlockPos(x + rand.nextInt(5 + 1 + 5) - 5, minY + rand.nextInt(maxY - minY), z + rand.nextInt(5 + 1 + 5) - 5);
                pluton.generate(world, rand, pos);
                Geolosys.chunkOreGen.addChunk(new ChunkPos(x / 16, z / 16), world, getSampleForOre(state));
            }
        }

        private IBlockState getSampleForOre(IBlockState state)
        {
            if (state.getBlock() == Geolosys.ORE)
                return Geolosys.ORE_SAMPLE.getStateFromMeta(state.getBlock().getMetaFromState(state));
            else if (state.getBlock() == Geolosys.ORE_VANILLA)
                return Geolosys.ORE_SAMPLE_VANILLA.getStateFromMeta(state.getBlock().getMetaFromState(state));
            else
                return Blocks.AIR.getDefaultState();
        }
    }

    public static ArrayList<OreGen> orespawnList = new ArrayList();

    public static OreGen addOreGen(IBlockState state, int maxVeinSize, int minY, int maxY, int weight)
    {
        OreGen gen = new OreGen(state, maxVeinSize, Blocks.STONE, minY, maxY, weight);
        orespawnList.add(gen);
        return gen;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!isDIMBlacklisted(world.provider.getDimension()) && orespawnList.size() > 0)
            orespawnList.get(random.nextInt(orespawnList.size())).generate(world, random, (chunkX * 16), (chunkZ * 16));
    }

    public boolean isDIMBlacklisted(int dim)
    {
        for (int d : Geolosys.config.blacklistedDIMs)
            if (d == dim)
                return true;
        return false;
    }
}
