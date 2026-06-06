//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.multiplayer.prediction;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockStatePredictionHandler implements AutoCloseable {
    private final Long2ObjectOpenHashMap<ServerVerifiedState> serverVerifiedStates = new Long2ObjectOpenHashMap();
    private int currentSequenceNr;
    private boolean isPredicting;

    public BlockStatePredictionHandler() {
    }

    public void retainKnownServerState(BlockPos p_233868_, BlockState p_233869_, LocalPlayer p_233870_) {
        this.serverVerifiedStates.compute(p_233868_.asLong(), (p_289242_, p_289243_) -> {
            return p_289243_ != null ? p_289243_.setSequence(this.currentSequenceNr) : new ServerVerifiedState(this.currentSequenceNr, p_233869_, p_233870_.position());
        });
    }

    public boolean updateKnownServerState(BlockPos p_233865_, BlockState p_233866_) {
        ServerVerifiedState $$2 = (ServerVerifiedState)this.serverVerifiedStates.get(p_233865_.asLong());
        if ($$2 == null) {
            return false;
        } else {
            $$2.setBlockState(p_233866_);
            return true;
        }
    }

    public void endPredictionsUpTo(int p_233857_, ClientLevel p_233858_) {
        ObjectIterator<Long2ObjectMap.Entry<ServerVerifiedState>> $$2 = this.serverVerifiedStates.long2ObjectEntrySet().iterator();

        while($$2.hasNext()) {
            Long2ObjectMap.Entry<ServerVerifiedState> $$3 = (Long2ObjectMap.Entry)$$2.next();
            ServerVerifiedState $$4 = (ServerVerifiedState)$$3.getValue();
            if ($$4.sequence <= p_233857_) {
                BlockPos $$5 = BlockPos.of($$3.getLongKey());
                $$2.remove();
                p_233858_.syncBlockState($$5, $$4.blockState, $$4.playerPos);
            }
        }

    }

    public BlockStatePredictionHandler startPredicting() {
        ++this.currentSequenceNr;
        this.isPredicting = true;
        return this;
    }

    public void close() {
        this.isPredicting = false;
    }

    public int currentSequence() {
        return this.currentSequenceNr;
    }

    public boolean isPredicting() {
        return this.isPredicting;
    }

    @OnlyIn(Dist.CLIENT)
    static class ServerVerifiedState {
        final Vec3 playerPos;
        int sequence;
        BlockState blockState;

        ServerVerifiedState(int p_233878_, BlockState p_233879_, Vec3 p_233880_) {
            this.sequence = p_233878_;
            this.blockState = p_233879_;
            this.playerPos = p_233880_;
        }

        ServerVerifiedState setSequence(int p_233882_) {
            this.sequence = p_233882_;
            return this;
        }

        void setBlockState(BlockState p_233884_) {
            this.blockState = p_233884_;
        }
    }
}
