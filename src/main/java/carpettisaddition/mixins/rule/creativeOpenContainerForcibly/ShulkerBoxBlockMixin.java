/*
 * This file is part of the Carpet TIS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * Carpet TIS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet TIS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet TIS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */

package carpettisaddition.mixins.rule.creativeOpenContainerForcibly;

import carpettisaddition.helpers.rule.creativeOpenContainerForcibly.CreativeOpenContainerForciblyHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC >= 11700
//$$ import net.minecraft.block.entity.ShulkerBoxBlockEntity;
//$$ import org.spongepowered.asm.mixin.Shadow;
//#else
import net.minecraft.util.math.Box;
//#endif

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin
{
	//#if MC >= 11700
	//$$ @Shadow
	//$$ private static boolean canOpen(BlockState state, World world, BlockPos pos, ShulkerBoxBlockEntity entity)
	//$$ {
	//$$ 	return false;
	//$$ }
	//#endif

	@Redirect(
			//#if MC >= 11500
			method = "onUse",
			//#else
			//$$ method = "activate",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11700
					//$$ target = "Lnet/minecraft/block/ShulkerBoxBlock;canOpen(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;)Z"
					//#elseif MC >= 11600
					//$$ target = "Lnet/minecraft/world/World;isSpaceEmpty(Lnet/minecraft/util/math/Box;)Z"
					//#else
					target = "Lnet/minecraft/world/World;doesNotCollide(Lnet/minecraft/util/math/Box;)Z"
					//#endif
			)
	)
	private boolean noCollideOrCreative(
			//#if MC >= 11700
			//$$ BlockState state, World world, BlockPos pos, ShulkerBoxBlockEntity shulkerBoxBlockEntity, /* parent method parameters -> */ BlockState state2, World world2, BlockPos pos2, PlayerEntity player, Hand hand, BlockHitResult hit
			//#else
			World world, Box box, /* parent method parameters -> */ BlockState state, World world2, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit
			//#endif
	)
	{
		if (CreativeOpenContainerForciblyHelper.canOpenForcibly(player))
		{
			return true;
		}

		// vanilla
		//#if MC >= 11700
		//$$ return canOpen(state, world, pos, shulkerBoxBlockEntity);
		//#else
		return world.doesNotCollide(box);
		//#endif
	}
}
