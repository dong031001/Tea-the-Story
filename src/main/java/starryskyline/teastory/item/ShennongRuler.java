package starryskyline.teastory.item;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import starryskyline.teastory.common.ConfigLoader;
import starryskyline.teastory.creativetab.CreativeTabsLoader;

public class ShennongRuler extends ItemSword
{
	public static final Item.ToolMaterial SHENNONGTOOL = EnumHelper.addToolMaterial("SHENNONGTOOL", 3, 768, 8.0F, 1.0F, 10);
	public ShennongRuler()
    {
		super(SHENNONGTOOL);
		this.setCreativeTab(CreativeTabsLoader.tabteastory);
		this.setMaxStackSize(1);
		this.setUnlocalizedName("shennong_ruler");
	}
	
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean b)
    {
        list.add(I18n.translateToLocal("teastory.tooltip.shennong_ruler"));
    }
	
	@SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
		if(!player.worldObj.isRemote)
        {
		    ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 1));
        }
        return false;
    }
	
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		if(!worldIn.isRemote)
        {
        	playerIn.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0)); 
        }
		if(!playerIn.capabilities.isCreativeMode)
		{
		    itemStackIn.setItemDamage(itemStackIn.getItemDamage() + 5);
		    if (itemStackIn.getItemDamage() > 768)
		    {
		    	--itemStackIn.stackSize;
		    }
		}
        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
    }
}
