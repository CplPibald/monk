package com.rwtema.monkmod.advancements.criteria;

import com.rwtema.monkmod.MonkManager;
import com.rwtema.monkmod.advancements.MonkRequirement;
import com.rwtema.monkmod.data.MonkData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MonkRequirementKissCreeper extends MonkRequirement {
	public MonkRequirementKissCreeper(int level) {
		super(level);
	}


	@SubscribeEvent
	public void onRightClickAnimal(PlayerInteractEvent.EntityInteract event) {
		Entity entity = event.getTarget();
		if (!(entity instanceof EntityCreeper)) return;
		EntityPlayer entityPlayer = event.getEntityPlayer();

		if (!entityPlayer.getHeldItem(event.getHand()).isEmpty()) return;

		MonkData monkData = MonkManager.get(entityPlayer);
		if (monkData.getLevel() == (this.levelToGrant - 1)) {
			if (event.getWorld().isRemote) {
				event.setCanceled(true);
				event.setCancellationResult(EnumActionResult.SUCCESS);
			}else{
				NBTTagCompound entityData = entity.getEntityData();
				NBTTagList monkPet = entityData.getTagList("MonkPet", Constants.NBT.TAG_STRING);
				String uniqueIdString = entityPlayer.getCachedUniqueIdString();
				for (int i = 0; i < monkPet.tagCount(); i++) {
					String stringTagAt = monkPet.getStringTagAt(i);
					if (stringTagAt.equals(uniqueIdString)) {
						return;
					}
				}
				monkPet.appendTag(new NBTTagString(uniqueIdString));
				entityData.setTag("MonkPet", monkPet);
				((EntityCreeper) entity).setNoAI(true);

				entityPlayer.sendMessage(new TextComponentTranslation("chat.type.text", entity.getDisplayName(), new TextComponentTranslation("monk.blush")));

				grantLevel(((EntityPlayerMP) entityPlayer));

				event.setCanceled(true);
				event.setCancellationResult(EnumActionResult.SUCCESS);
			}
		}

	}
}