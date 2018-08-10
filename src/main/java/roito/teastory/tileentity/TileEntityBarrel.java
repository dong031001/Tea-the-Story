package roito.teastory.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import roito.teastory.block.Barrel;
import roito.teastory.block.BlockLoader;
import roito.teastory.config.ConfigMain;
import roito.teastory.helper.EntironmentHelper;
import roito.teastory.item.ItemLoader;

public class TileEntityBarrel extends TileEntity implements ITickable
{
	protected int realTicks = 0;
	protected int totalTicks = 0;
	
	protected ItemStackHandler leafInventory = new ItemStackHandler(3);

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability))
		{
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability))
		{
			T result = (T) (leafInventory);
			return result;
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.leafInventory.deserializeNBT(compound.getCompoundTag("LeafInventory"));
		this.realTicks = compound.getInteger("RealTick");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound.setTag("LeafInventory", this.leafInventory.serializeNBT());
		compound.setInteger("RealTick", this.realTicks);
		return super.writeToNBT(compound);
	}
	
	@Override
	public void update()
	{
		if (!this.getWorld().isRemote)
		{
			Biome biome = this.getWorld().getBiome(pos);
			int basicTick = EntironmentHelper.getFermentationTicks(biome.getFloatTemperature(pos), biome.getRainfall());
			int trueTick = basicTick;
			int step = this.getBlockMetadata();
			if (this.getWorld().canSeeSky(pos.up()))
			{
				trueTick *= 1.5F;
			}
			int halfDriedLeafNumber = getLeafNumber(ItemLoader.half_dried_tea);
			if (halfDriedLeafNumber > 0)
			{
				if (realTicks <= totalTicks / 2 && step != 1)
				{
					Barrel.setState(1, worldObj, pos);
				}
				totalTicks = (halfDriedLeafNumber < 16 ? 16 : halfDriedLeafNumber) * trueTick;
				jugde(ItemLoader.half_dried_tea, ItemLoader.black_tea_leaf);
				if (realTicks >= totalTicks / 2 && step != 2)
				{
					Barrel.setState(2, worldObj, pos);
				}
				this.markDirty();
				return;
			}
			int blackLeafNumber = getLeafNumber(ItemLoader.black_tea_leaf);
			if (blackLeafNumber > 0)
			{
				if (step != 3)
				{
					Barrel.setState(3, worldObj, pos);
				}
				totalTicks = (blackLeafNumber < 16 ? 16 : blackLeafNumber) * trueTick;
				jugde(ItemLoader.black_tea_leaf, ItemLoader.puer_tea_leaf);
				this.markDirty();
				return;
			}
			int puerLeafNumber = getLeafNumber(ItemLoader.puer_tea_leaf);
			if (puerLeafNumber > 0)
			{
				if (step != 4)
				{
					Barrel.setState(4, worldObj, pos);
				}
				this.markDirty();
				return;
			}
			realTicks = 0;
			if (step != 0)
			{
				Barrel.setState(0, worldObj, pos);
			}
			this.markDirty();
		}
	}
	
	private void jugde(Item before, Item after)
	{
		realTicks++;
		if (this.realTicks >= this.totalTicks)
		{
			for (int i = 0; i < 3; i++)
			{
				if (leafInventory.getStackInSlot(i) != null && leafInventory.getStackInSlot(i).getItem() == before)
				{
					leafInventory.setStackInSlot(i, new ItemStack(after, leafInventory.getStackInSlot(i).stackSize));
				}
			}
			realTicks = 0;
		}
	}
	
	public int getTotalTicks()
	{
		return this.totalTicks;
	}
	
	public int getRealTicks()
	{
		return this.realTicks;
	}
	
	public int getLeafNumber(Item leaf)
	{
		int leafNumber = 0;
		for (int i = 0; i < 3; i++)
		{
			if (leafInventory.getStackInSlot(i) != null && leafInventory.getStackInSlot(i).getItem() == leaf)
			{
				leafNumber += leafInventory.getStackInSlot(i).stackSize;
			}
		}
		return leafNumber;
	}
}