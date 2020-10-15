package carpettisaddition.mixins.logger.microtick.tickstages;

import carpettisaddition.logging.loggers.microtick.MicroTickLoggerManager;
import carpettisaddition.logging.loggers.microtick.tickstages.BlockEventTickStageExtra;
import carpettisaddition.logging.loggers.microtick.tickstages.StringTickStage;
import carpettisaddition.logging.loggers.microtick.tickstages.TileTickTickStageExtra;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.server.world.BlockAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.ScheduledTick;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin
{

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/border/WorldBorder;tick()V"
			)
	)
	private void onStageWorldBorder(CallbackInfo ci)
	{
		MicroTickLoggerManager.setTickStage((ServerWorld)(Object)this, "WorldBorder");
	}

	/*
	 * ------------------
	 *  Tile Tick starts
	 * ------------------
	 */

	@Inject(
			method = "tick",
			at = @At(
					value = "CONSTANT",
					args = "stringValue=tickPending"
			)
	)
	private void onStageTileTick(CallbackInfo ci)
	{
		MicroTickLoggerManager.setTickStage((ServerWorld)(Object)this, "TileTick");
	}

	private int tileTickOrderCounter = 0;

	@Inject(
			method = "tick",
			at = @At(
					value = "CONSTANT",
					args = "stringValue=tickPending"
			)
	)
	private void onEnterTileTickStage(CallbackInfo ci)
	{
		this.tileTickOrderCounter = 0;
	}

	@Inject(method = "tickBlock", at = @At("HEAD"))
	private void beforeExecuteTileTickEvent(ScheduledTick<Block> event, CallbackInfo ci)
	{
		MicroTickLoggerManager.setTickStageDetail((ServerWorld)(Object)this, String.valueOf(event.priority.getIndex()));
		MicroTickLoggerManager.setTickStageExtra((ServerWorld)(Object)this, new TileTickTickStageExtra((ServerWorld)(Object)this, event, this.tileTickOrderCounter++));
	}

	@Inject(method = "tickBlock", at = @At("RETURN"))
	private void afterExecuteTileTickEvent(ScheduledTick<Block> event, CallbackInfo ci)
	{
		MicroTickLoggerManager.setTickStageDetail((ServerWorld)(Object)this, null);
		MicroTickLoggerManager.setTickStageExtra((ServerWorld)(Object)this, null);
	}

	/*
	 * ----------------
	 *  Tile Tick ends
	 * ----------------
	 */

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/raid/RaidManager;tick()V"
			)
	)
	private void onStageRaid(CallbackInfo ci)
	{
		MicroTickLoggerManager.setTickStage((ServerWorld)(Object)this, "Raid");
	}

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/WanderingTraderManager;tick()V"
			)
	)
	void onStageWanderingTrader(CallbackInfo ci)
	{
		MicroTickLoggerManager.setTickStage((ServerWorld)(Object)this, "WanderingTrader");
	}

	/*
	 * --------------------
	 *  Block Event starts
	 * --------------------
	 */

	@Inject(
			method = "sendBlockActions",
			at = @At("HEAD")
	)
	private void onStageBlockEvent(CallbackInfo ci)
	{
		MicroTickLoggerManager.setTickStage((ServerWorld)(Object)this, "BlockEvent");
	}

	@Shadow
	@Final
	private ObjectLinkedOpenHashSet<BlockAction> pendingBlockActions;

	private int blockEventOrderCounter;
	private int blockEventDepth;
	private int blockEventCurrentDepthCounter;
	private int blockEventCurrentDepthSize;

	@Inject(
			method = "sendBlockActions",
			at = @At("HEAD")
	)
	private void onEnterBlockEventStage(CallbackInfo ci)
	{
		this.blockEventOrderCounter = 0;
		this.blockEventCurrentDepthCounter = 0;
		this.blockEventDepth = 0;
		this.blockEventCurrentDepthSize = this.pendingBlockActions.size();
	}

	@Inject(method = "method_14174", at = @At("HEAD"))
	private void beforeBlockEventExecuted(BlockAction blockAction, CallbackInfoReturnable<Boolean> cir)
	{
		MicroTickLoggerManager.setTickStageDetail((ServerWorld)(Object)this, String.valueOf(this.blockEventDepth));
		MicroTickLoggerManager.setTickStageExtra((ServerWorld)(Object)this, new BlockEventTickStageExtra((ServerWorld)(Object)this, blockAction, this.blockEventOrderCounter++, this.blockEventDepth));
	}

	@Inject(method = "method_14174", at = @At("RETURN"))
	private void afterBlockEventExecuted(BlockAction blockAction, CallbackInfoReturnable<Boolean> cir)
	{
		MicroTickLoggerManager.setTickStageDetail((ServerWorld)(Object)this, null);
		MicroTickLoggerManager.setTickStageExtra((ServerWorld)(Object)this, null);
		this.blockEventCurrentDepthCounter++;
		if (this.blockEventCurrentDepthCounter == this.blockEventCurrentDepthSize)
		{
			this.blockEventDepth++;
			this.blockEventCurrentDepthSize = this.pendingBlockActions.size();
			this.blockEventCurrentDepthCounter = 0;
		}
	}

	/*
	 * ------------------
	 *  Block Event ends
	 * ------------------
	 */

	@Inject(
			method = "tick",
			at = @At(
					value = "CONSTANT",
					args = "stringValue=entities"
			)
	)
	private void onStageEntitiesWeather(CallbackInfo ci)
	{
		MicroTickLoggerManager.setTickStage((ServerWorld)(Object)this, "Entity");
		MicroTickLoggerManager.setTickStageExtra((ServerWorld)(Object)this, StringTickStage.ENTITY_WEATHER_EFFECT);
	}

	@Inject(
			method = "tick",
			at = @At(
					value = "CONSTANT",
					args = "stringValue=regular"
			)
	)
	private void onStageEntitiesRegular(CallbackInfo ci)
	{
		MicroTickLoggerManager.setTickStageExtra((ServerWorld)(Object)this, StringTickStage.ENTITY_REGULAR);
	}

	@Inject(
			method = "tickChunk",
			at = @At("HEAD")
	)
	private void onTickChunk(CallbackInfo ci)
	{
		MicroTickLoggerManager.setTickStage((ServerWorld)(Object)this, "RandomTick&Climate");
	}

	@Inject(
			method = "tickChunk",
			at = @At(
					value = "CONSTANT",
					args = "stringValue=thunder"
			)
	)
	private void onStageDetailThunder(CallbackInfo ci)
	{
		MicroTickLoggerManager.setTickStageDetail((ServerWorld)(Object)this, "Thunder");
	}

	@Inject(
			method = "tickChunk",
			at = @At(
					value = "CONSTANT",
					args = "stringValue=iceandsnow"
			)
	)
	private void onStageDetailIceAndSnow(CallbackInfo ci)
	{
		MicroTickLoggerManager.setTickStageDetail((ServerWorld)(Object)this, "Ice&Snow");
	}

	@Inject(
			method = "tickChunk",
			at = @At(
					value = "CONSTANT",
					args = "stringValue=tickBlocks"
			)
	)
	private void onStageDetailRandomTick(CallbackInfo ci)
	{
		MicroTickLoggerManager.setTickStageDetail((ServerWorld)(Object)this, "RandomTick");
	}
}
