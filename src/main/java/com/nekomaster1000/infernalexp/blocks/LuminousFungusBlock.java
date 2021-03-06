package com.nekomaster1000.infernalexp.blocks;

import java.util.Random;

import com.nekomaster1000.infernalexp.init.IEBlocks;
import com.nekomaster1000.infernalexp.init.IEConfiguredFeatures;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.HugeFungusConfig;
import net.minecraft.world.server.ServerWorld;

public class LuminousFungusBlock extends HorizontalBushBlock implements IGrowable {
    protected static final VoxelShape FLOOR_SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
    protected static final VoxelShape CEILING_SHAPE = Block.makeCuboidShape(5.0D, 6.0D, 5.0D, 11.0D, 16.0D, 11.0D);

    public LuminousFungusBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACE, AttachFace.FLOOR).with(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return
                state.isIn(IEBlocks.GLOWDUST_SAND.get()) || state.isIn(Blocks.SAND) || state.isIn(Blocks.RED_SAND)
                || state.isIn(Blocks.GRASS) || state.isIn(Blocks.GRASS_BLOCK) ||
                state.isIn(Blocks.DIRT) || state.isIn(Blocks.COARSE_DIRT) || state.isIn(Blocks.FARMLAND) ||
                state.isIn(Blocks.PODZOL) || state.isIn(Blocks.MYCELIUM) ||
                state.isIn(Blocks.CRIMSON_NYLIUM) || state.isIn(Blocks.WARPED_NYLIUM) ||
                state.isIn(Blocks.SOUL_SOIL) || state.isIn(Blocks.GLOWSTONE) || state.isIn(IEBlocks.DIMSTONE.get()) ||
                state.isIn(IEBlocks.DULLSTONE.get()) || state.isIn(IEBlocks.DULLTHORNS.get())
                ;
    }

    public boolean canAttach(IWorldReader reader, BlockPos pos, Direction direction) {
        BlockPos blockpos = pos.offset(direction);
        return isValidGround(reader.getBlockState(blockpos), reader, blockpos);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return !state.get(FACE).equals(AttachFace.WALL) && canAttach(worldIn, pos, getFacing(state).getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Vector3d vector3d = state.getOffset(worldIn, pos);

        switch(state.get(FACE)){
            case FLOOR:
                return FLOOR_SHAPE.withOffset(vector3d.x, vector3d.y, vector3d.z);
            case CEILING:
            default:
                return CEILING_SHAPE.withOffset(vector3d.x, vector3d.y, vector3d.z);
        }
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builderIn) {
        builderIn.add(HORIZONTAL_FACING, FACE);
    }
    
	/**
	 * Whether this IGrowable can grow
	 */
	@Override
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		Block block = ((HugeFungusConfig) (IEConfiguredFeatures.DULLTHORN_TREE_PLANTED).config).field_236303_f_.getBlock();
		Block block1 = worldIn.getBlockState(pos.down()).getBlock();
		return block1 == block;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return (double) rand.nextFloat() < 0.4D;
	}

	@Override
	public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
		IEConfiguredFeatures.DULLTHORN_TREE_PLANTED.generate(worldIn, worldIn.getChunkProvider().getChunkGenerator(), rand, pos);
	}
}
