//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.gametest.framework;

import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class GameTestHelper {
    private final GameTestInfo testInfo;
    private boolean finalCheckAdded;

    public GameTestHelper(GameTestInfo p_127597_) {
        this.testInfo = p_127597_;
    }

    public ServerLevel getLevel() {
        return this.testInfo.getLevel();
    }

    public BlockState getBlockState(BlockPos p_177233_) {
        return this.getLevel().getBlockState(this.absolutePos(p_177233_));
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos p_177348_) {
        return this.getLevel().getBlockEntity(this.absolutePos(p_177348_));
    }

    public void killAllEntities() {
        this.killAllEntitiesOfClass(Entity.class);
    }

    public void killAllEntitiesOfClass(Class p_289538_) {
        AABB $$1 = this.getBounds();
        List<Entity> $$2 = this.getLevel().getEntitiesOfClass(p_289538_, $$1.inflate(1.0), (p_177131_) -> {
            return !(p_177131_ instanceof Player);
        });
        $$2.forEach(Entity::kill);
    }

    public ItemEntity spawnItem(Item p_177190_, float p_177191_, float p_177192_, float p_177193_) {
        ServerLevel $$4 = this.getLevel();
        Vec3 $$5 = this.absoluteVec(new Vec3((double)p_177191_, (double)p_177192_, (double)p_177193_));
        ItemEntity $$6 = new ItemEntity($$4, $$5.x, $$5.y, $$5.z, new ItemStack(p_177190_, 1));
        $$6.setDeltaMovement(0.0, 0.0, 0.0);
        $$4.addFreshEntity($$6);
        return $$6;
    }

    public ItemEntity spawnItem(Item p_251435_, BlockPos p_250287_) {
        return this.spawnItem(p_251435_, (float)p_250287_.getX(), (float)p_250287_.getY(), (float)p_250287_.getZ());
    }

    public <E extends Entity> E spawn(EntityType<E> p_177177_, BlockPos p_177178_) {
        return this.spawn(p_177177_, Vec3.atBottomCenterOf(p_177178_));
    }

    public <E extends Entity> E spawn(EntityType<E> p_177174_, Vec3 p_177175_) {
        ServerLevel $$2 = this.getLevel();
        E $$3 = p_177174_.create($$2);
        if ($$3 == null) {
            throw new NullPointerException("Failed to create entity " + p_177174_.builtInRegistryHolder().key().location());
        } else {
            if ($$3 instanceof Mob) {
                Mob $$4 = (Mob)$$3;
                $$4.setPersistenceRequired();
            }

            Vec3 $$5 = this.absoluteVec(p_177175_);
            $$3.moveTo($$5.x, $$5.y, $$5.z, $$3.getYRot(), $$3.getXRot());
            $$2.addFreshEntity($$3);
            return $$3;
        }
    }

    public <E extends Entity> E spawn(EntityType<E> p_177169_, int p_177170_, int p_177171_, int p_177172_) {
        return this.spawn(p_177169_, new BlockPos(p_177170_, p_177171_, p_177172_));
    }

    public <E extends Entity> E spawn(EntityType<E> p_177164_, float p_177165_, float p_177166_, float p_177167_) {
        return this.spawn(p_177164_, new Vec3((double)p_177165_, (double)p_177166_, (double)p_177167_));
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> p_177330_, BlockPos p_177331_) {
        E $$2 = (Mob)this.spawn(p_177330_, p_177331_);
        $$2.removeFreeWill();
        return $$2;
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> p_177322_, int p_177323_, int p_177324_, int p_177325_) {
        return this.spawnWithNoFreeWill(p_177322_, new BlockPos(p_177323_, p_177324_, p_177325_));
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> p_177327_, Vec3 p_177328_) {
        E $$2 = (Mob)this.spawn(p_177327_, p_177328_);
        $$2.removeFreeWill();
        return $$2;
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> p_177317_, float p_177318_, float p_177319_, float p_177320_) {
        return this.spawnWithNoFreeWill(p_177317_, new Vec3((double)p_177318_, (double)p_177319_, (double)p_177320_));
    }

    public GameTestSequence walkTo(Mob p_177186_, BlockPos p_177187_, float p_177188_) {
        return this.startSequence().thenExecuteAfter(2, () -> {
            Path $$3 = p_177186_.getNavigation().createPath((BlockPos)this.absolutePos(p_177187_), 0);
            p_177186_.getNavigation().moveTo($$3, (double)p_177188_);
        });
    }

    public void pressButton(int p_177104_, int p_177105_, int p_177106_) {
        this.pressButton(new BlockPos(p_177104_, p_177105_, p_177106_));
    }

    public void pressButton(BlockPos p_177386_) {
        this.assertBlockState(p_177386_, (p_177212_) -> {
            return p_177212_.is(BlockTags.BUTTONS);
        }, () -> {
            return "Expected button";
        });
        BlockPos $$1 = this.absolutePos(p_177386_);
        BlockState $$2 = this.getLevel().getBlockState($$1);
        ButtonBlock $$3 = (ButtonBlock)$$2.getBlock();
        $$3.press($$2, this.getLevel(), $$1);
    }

    public void useBlock(BlockPos p_177409_) {
        this.useBlock(p_177409_, this.makeMockPlayer());
    }

    public void useBlock(BlockPos p_250131_, Player p_251507_) {
        BlockPos $$2 = this.absolutePos(p_250131_);
        this.useBlock(p_250131_, p_251507_, new BlockHitResult(Vec3.atCenterOf($$2), Direction.NORTH, $$2, true));
    }

    public void useBlock(BlockPos p_262023_, Player p_261901_, BlockHitResult p_262040_) {
        BlockPos $$3 = this.absolutePos(p_262023_);
        BlockState $$4 = this.getLevel().getBlockState($$3);
        InteractionResult $$5 = $$4.use(this.getLevel(), p_261901_, InteractionHand.MAIN_HAND, p_262040_);
        if (!$$5.consumesAction()) {
            UseOnContext $$6 = new UseOnContext(p_261901_, InteractionHand.MAIN_HAND, p_262040_);
            p_261901_.getItemInHand(InteractionHand.MAIN_HAND).useOn($$6);
        }

    }

    public LivingEntity makeAboutToDrown(LivingEntity p_177184_) {
        p_177184_.setAirSupply(0);
        p_177184_.setHealth(0.25F);
        return p_177184_;
    }

    public Player makeMockSurvivalPlayer() {
        return new Player(this.getLevel(), BlockPos.ZERO, 0.0F, new GameProfile(UUID.randomUUID(), "test-mock-player")) {
            public boolean isSpectator() {
                return false;
            }

            public boolean isCreative() {
                return false;
            }
        };
    }

    public LivingEntity withLowHealth(LivingEntity p_286794_) {
        p_286794_.setHealth(0.25F);
        return p_286794_;
    }

    public Player makeMockPlayer() {
        return new Player(this.getLevel(), BlockPos.ZERO, 0.0F, new GameProfile(UUID.randomUUID(), "test-mock-player")) {
            public boolean isSpectator() {
                return false;
            }

            public boolean isCreative() {
                return true;
            }

            public boolean isLocalPlayer() {
                return true;
            }
        };
    }

    public ServerPlayer makeMockServerPlayerInLevel() {
        ServerPlayer $$0 = new ServerPlayer(this.getLevel().getServer(), this.getLevel(), new GameProfile(UUID.randomUUID(), "test-mock-player")) {
            public boolean isSpectator() {
                return false;
            }

            public boolean isCreative() {
                return true;
            }
        };
        this.getLevel().getServer().getPlayerList().placeNewPlayer(new Connection(PacketFlow.SERVERBOUND), $$0);
        return $$0;
    }

    public void pullLever(int p_177303_, int p_177304_, int p_177305_) {
        this.pullLever(new BlockPos(p_177303_, p_177304_, p_177305_));
    }

    public void pullLever(BlockPos p_177422_) {
        this.assertBlockPresent(Blocks.LEVER, p_177422_);
        BlockPos $$1 = this.absolutePos(p_177422_);
        BlockState $$2 = this.getLevel().getBlockState($$1);
        LeverBlock $$3 = (LeverBlock)$$2.getBlock();
        $$3.pull($$2, this.getLevel(), $$1);
    }

    public void pulseRedstone(BlockPos p_177235_, long p_177236_) {
        this.setBlock(p_177235_, Blocks.REDSTONE_BLOCK);
        this.runAfterDelay(p_177236_, () -> {
            this.setBlock(p_177235_, Blocks.AIR);
        });
    }

    public void destroyBlock(BlockPos p_177435_) {
        this.getLevel().destroyBlock(this.absolutePos(p_177435_), false, (Entity)null);
    }

    public void setBlock(int p_177108_, int p_177109_, int p_177110_, Block p_177111_) {
        this.setBlock(new BlockPos(p_177108_, p_177109_, p_177110_), p_177111_);
    }

    public void setBlock(int p_177113_, int p_177114_, int p_177115_, BlockState p_177116_) {
        this.setBlock(new BlockPos(p_177113_, p_177114_, p_177115_), p_177116_);
    }

    public void setBlock(BlockPos p_177246_, Block p_177247_) {
        this.setBlock(p_177246_, p_177247_.defaultBlockState());
    }

    public void setBlock(BlockPos p_177253_, BlockState p_177254_) {
        this.getLevel().setBlock(this.absolutePos(p_177253_), p_177254_, 3);
    }

    public void setNight() {
        this.setDayTime(13000);
    }

    public void setDayTime(int p_177102_) {
        this.getLevel().setDayTime((long)p_177102_);
    }

    public void assertBlockPresent(Block p_177204_, int p_177205_, int p_177206_, int p_177207_) {
        this.assertBlockPresent(p_177204_, new BlockPos(p_177205_, p_177206_, p_177207_));
    }

    public void assertBlockPresent(Block p_177209_, BlockPos p_177210_) {
        BlockState $$2 = this.getBlockState(p_177210_);
        Predicate var10002 = (p_177216_) -> {
            return $$2.is(p_177209_);
        };
        String var10003 = p_177209_.getName().getString();
        this.assertBlock(p_177210_, var10002, "Expected " + var10003 + ", got " + $$2.getBlock().getName().getString());
    }

    public void assertBlockNotPresent(Block p_177337_, int p_177338_, int p_177339_, int p_177340_) {
        this.assertBlockNotPresent(p_177337_, new BlockPos(p_177338_, p_177339_, p_177340_));
    }

    public void assertBlockNotPresent(Block p_177342_, BlockPos p_177343_) {
        this.assertBlock(p_177343_, (p_177251_) -> {
            return !this.getBlockState(p_177343_).is(p_177342_);
        }, "Did not expect " + p_177342_.getName().getString());
    }

    public void succeedWhenBlockPresent(Block p_177378_, int p_177379_, int p_177380_, int p_177381_) {
        this.succeedWhenBlockPresent(p_177378_, new BlockPos(p_177379_, p_177380_, p_177381_));
    }

    public void succeedWhenBlockPresent(Block p_177383_, BlockPos p_177384_) {
        this.succeedWhen(() -> {
            this.assertBlockPresent(p_177383_, p_177384_);
        });
    }

    public void assertBlock(BlockPos p_177272_, Predicate<Block> p_177273_, String p_177274_) {
        this.assertBlock(p_177272_, p_177273_, () -> {
            return p_177274_;
        });
    }

    public void assertBlock(BlockPos p_177276_, Predicate<Block> p_177277_, Supplier<String> p_177278_) {
        this.assertBlockState(p_177276_, (p_177296_) -> {
            return p_177277_.test(p_177296_.getBlock());
        }, p_177278_);
    }

    public <T extends Comparable<T>> void assertBlockProperty(BlockPos p_177256_, Property<T> p_177257_, T p_177258_) {
        BlockState $$3 = this.getBlockState(p_177256_);
        boolean $$4 = $$3.hasProperty(p_177257_);
        if (!$$4 || !$$3.getValue(p_177257_).equals(p_177258_)) {
            String $$5 = $$4 ? "was " + $$3.getValue(p_177257_) : "property " + p_177257_.getName() + " is missing";
            String $$6 = String.format(Locale.ROOT, "Expected property %s to be %s, %s", p_177257_.getName(), p_177258_, $$5);
            throw new GameTestAssertPosException($$6, this.absolutePos(p_177256_), p_177256_, this.testInfo.getTick());
        }
    }

    public <T extends Comparable<T>> void assertBlockProperty(BlockPos p_177260_, Property<T> p_177261_, Predicate<T> p_177262_, String p_177263_) {
        this.assertBlockState(p_177260_, (p_277264_) -> {
            if (!p_277264_.hasProperty(p_177261_)) {
                return false;
            } else {
                T $$3 = p_277264_.getValue(p_177261_);
                return p_177262_.test($$3);
            }
        }, () -> {
            return p_177263_;
        });
    }

    public void assertBlockState(BlockPos p_177358_, Predicate<BlockState> p_177359_, Supplier<String> p_177360_) {
        BlockState $$3 = this.getBlockState(p_177358_);
        if (!p_177359_.test($$3)) {
            throw new GameTestAssertPosException((String)p_177360_.get(), this.absolutePos(p_177358_), p_177358_, this.testInfo.getTick());
        }
    }

    public void assertRedstoneSignal(BlockPos p_289644_, Direction p_289642_, IntPredicate p_289645_, Supplier<String> p_289684_) {
        BlockPos $$4 = this.absolutePos(p_289644_);
        ServerLevel $$5 = this.getLevel();
        BlockState $$6 = $$5.getBlockState($$4);
        int $$7 = $$6.getSignal($$5, $$4, p_289642_);
        if (!p_289645_.test($$7)) {
            throw new GameTestAssertPosException((String)p_289684_.get(), $$4, p_289644_, this.testInfo.getTick());
        }
    }

    public void assertEntityPresent(EntityType<?> p_177157_) {
        List<? extends Entity> $$1 = this.getLevel().getEntities(p_177157_, this.getBounds(), Entity::isAlive);
        if ($$1.isEmpty()) {
            throw new GameTestAssertException("Expected " + p_177157_.toShortString() + " to exist");
        }
    }

    public void assertEntityPresent(EntityType<?> p_177370_, int p_177371_, int p_177372_, int p_177373_) {
        this.assertEntityPresent(p_177370_, new BlockPos(p_177371_, p_177372_, p_177373_));
    }

    public void assertEntityPresent(EntityType<?> p_177375_, BlockPos p_177376_) {
        BlockPos $$2 = this.absolutePos(p_177376_);
        List<? extends Entity> $$3 = this.getLevel().getEntities(p_177375_, new AABB($$2), Entity::isAlive);
        if ($$3.isEmpty()) {
            throw new GameTestAssertPosException("Expected " + p_177375_.toShortString(), $$2, p_177376_, this.testInfo.getTick());
        }
    }

    public void assertEntityPresent(EntityType<?> p_252010_, Vec3 p_249488_, Vec3 p_251186_) {
        List<? extends Entity> $$3 = this.getLevel().getEntities(p_252010_, new AABB(p_249488_, p_251186_), Entity::isAlive);
        if ($$3.isEmpty()) {
            throw new GameTestAssertPosException("Expected " + p_252010_.toShortString() + " between ", BlockPos.containing(p_249488_), BlockPos.containing(p_251186_), this.testInfo.getTick());
        }
    }

    public void assertEntitiesPresent(EntityType<?> p_239372_, BlockPos p_239373_, int p_239374_, double p_239375_) {
        BlockPos $$4 = this.absolutePos(p_239373_);
        List<? extends Entity> $$5 = this.getEntities(p_239372_, p_239373_, p_239375_);
        if ($$5.size() != p_239374_) {
            throw new GameTestAssertPosException("Expected " + p_239374_ + " entities of type " + p_239372_.toShortString() + ", actual number of entities found=" + $$5.size(), $$4, p_239373_, this.testInfo.getTick());
        }
    }

    public void assertEntityPresent(EntityType<?> p_177180_, BlockPos p_177181_, double p_177182_) {
        List<? extends Entity> $$3 = this.getEntities(p_177180_, p_177181_, p_177182_);
        if ($$3.isEmpty()) {
            BlockPos $$4 = this.absolutePos(p_177181_);
            throw new GameTestAssertPosException("Expected " + p_177180_.toShortString(), $$4, p_177181_, this.testInfo.getTick());
        }
    }

    public <T extends Entity> List<T> getEntities(EntityType<T> p_238400_, BlockPos p_238401_, double p_238402_) {
        BlockPos $$3 = this.absolutePos(p_238401_);
        return this.getLevel().getEntities(p_238400_, (new AABB($$3)).inflate(p_238402_), Entity::isAlive);
    }

    public void assertEntityInstancePresent(Entity p_177133_, int p_177134_, int p_177135_, int p_177136_) {
        this.assertEntityInstancePresent(p_177133_, new BlockPos(p_177134_, p_177135_, p_177136_));
    }

    public void assertEntityInstancePresent(Entity p_177141_, BlockPos p_177142_) {
        BlockPos $$2 = this.absolutePos(p_177142_);
        List<? extends Entity> $$3 = this.getLevel().getEntities(p_177141_.getType(), new AABB($$2), Entity::isAlive);
        $$3.stream().filter((p_177139_) -> {
            return p_177139_ == p_177141_;
        }).findFirst().orElseThrow(() -> {
            return new GameTestAssertPosException("Expected " + p_177141_.getType().toShortString(), $$2, p_177142_, this.testInfo.getTick());
        });
    }

    public void assertItemEntityCountIs(Item p_177199_, BlockPos p_177200_, double p_177201_, int p_177202_) {
        BlockPos $$4 = this.absolutePos(p_177200_);
        List<ItemEntity> $$5 = this.getLevel().getEntities(EntityType.ITEM, (new AABB($$4)).inflate(p_177201_), Entity::isAlive);
        int $$6 = 0;
        Iterator var9 = $$5.iterator();

        while(var9.hasNext()) {
            ItemEntity $$7 = (ItemEntity)var9.next();
            ItemStack $$8 = $$7.getItem();
            if ($$8.is(p_177199_)) {
                $$6 += $$8.getCount();
            }
        }

        if ($$6 != p_177202_) {
            throw new GameTestAssertPosException("Expected " + p_177202_ + " " + p_177199_.getDescription().getString() + " items to exist (found " + $$6 + ")", $$4, p_177200_, this.testInfo.getTick());
        }
    }

    public void assertItemEntityPresent(Item p_177195_, BlockPos p_177196_, double p_177197_) {
        BlockPos $$3 = this.absolutePos(p_177196_);
        List<? extends Entity> $$4 = this.getLevel().getEntities(EntityType.ITEM, (new AABB($$3)).inflate(p_177197_), Entity::isAlive);
        Iterator var7 = $$4.iterator();

        ItemEntity $$6;
        do {
            if (!var7.hasNext()) {
                throw new GameTestAssertPosException("Expected " + p_177195_.getDescription().getString() + " item", $$3, p_177196_, this.testInfo.getTick());
            }

            Entity $$5 = (Entity)var7.next();
            $$6 = (ItemEntity)$$5;
        } while(!$$6.getItem().getItem().equals(p_177195_));

    }

    public void assertItemEntityNotPresent(Item p_236779_, BlockPos p_236780_, double p_236781_) {
        BlockPos $$3 = this.absolutePos(p_236780_);
        List<? extends Entity> $$4 = this.getLevel().getEntities(EntityType.ITEM, (new AABB($$3)).inflate(p_236781_), Entity::isAlive);
        Iterator var7 = $$4.iterator();

        ItemEntity $$6;
        do {
            if (!var7.hasNext()) {
                return;
            }

            Entity $$5 = (Entity)var7.next();
            $$6 = (ItemEntity)$$5;
        } while(!$$6.getItem().getItem().equals(p_236779_));

        throw new GameTestAssertPosException("Did not expect " + p_236779_.getDescription().getString() + " item", $$3, p_236780_, this.testInfo.getTick());
    }

    public void assertEntityNotPresent(EntityType<?> p_177310_) {
        List<? extends Entity> $$1 = this.getLevel().getEntities(p_177310_, this.getBounds(), Entity::isAlive);
        if (!$$1.isEmpty()) {
            throw new GameTestAssertException("Did not expect " + p_177310_.toShortString() + " to exist");
        }
    }

    public void assertEntityNotPresent(EntityType<?> p_177398_, int p_177399_, int p_177400_, int p_177401_) {
        this.assertEntityNotPresent(p_177398_, new BlockPos(p_177399_, p_177400_, p_177401_));
    }

    public void assertEntityNotPresent(EntityType<?> p_177403_, BlockPos p_177404_) {
        BlockPos $$2 = this.absolutePos(p_177404_);
        List<? extends Entity> $$3 = this.getLevel().getEntities(p_177403_, new AABB($$2), Entity::isAlive);
        if (!$$3.isEmpty()) {
            throw new GameTestAssertPosException("Did not expect " + p_177403_.toShortString(), $$2, p_177404_, this.testInfo.getTick());
        }
    }

    public void assertEntityTouching(EntityType<?> p_177159_, double p_177160_, double p_177161_, double p_177162_) {
        Vec3 $$4 = new Vec3(p_177160_, p_177161_, p_177162_);
        Vec3 $$5 = this.absoluteVec($$4);
        Predicate<? super Entity> $$6 = (p_177346_) -> {
            return p_177346_.getBoundingBox().intersects($$5, $$5);
        };
        List<? extends Entity> $$7 = this.getLevel().getEntities(p_177159_, this.getBounds(), $$6);
        if ($$7.isEmpty()) {
            throw new GameTestAssertException("Expected " + p_177159_.toShortString() + " to touch " + $$5 + " (relative " + $$4 + ")");
        }
    }

    public void assertEntityNotTouching(EntityType<?> p_177312_, double p_177313_, double p_177314_, double p_177315_) {
        Vec3 $$4 = new Vec3(p_177313_, p_177314_, p_177315_);
        Vec3 $$5 = this.absoluteVec($$4);
        Predicate<? super Entity> $$6 = (p_177231_) -> {
            return !p_177231_.getBoundingBox().intersects($$5, $$5);
        };
        List<? extends Entity> $$7 = this.getLevel().getEntities(p_177312_, this.getBounds(), $$6);
        if ($$7.isEmpty()) {
            throw new GameTestAssertException("Did not expect " + p_177312_.toShortString() + " to touch " + $$5 + " (relative " + $$4 + ")");
        }
    }

    public <E extends Entity, T> void assertEntityData(BlockPos p_177238_, EntityType<E> p_177239_, Function<? super E, T> p_177240_, @Nullable T p_177241_) {
        BlockPos $$4 = this.absolutePos(p_177238_);
        List<E> $$5 = this.getLevel().getEntities(p_177239_, new AABB($$4), Entity::isAlive);
        if ($$5.isEmpty()) {
            throw new GameTestAssertPosException("Expected " + p_177239_.toShortString(), $$4, p_177238_, this.testInfo.getTick());
        } else {
            Iterator var7 = $$5.iterator();

            while(var7.hasNext()) {
                E $$6 = (Entity)var7.next();
                T $$7 = p_177240_.apply($$6);
                if ($$7 == null) {
                    if (p_177241_ != null) {
                        throw new GameTestAssertException("Expected entity data to be: " + p_177241_ + ", but was: " + $$7);
                    }
                } else if (!$$7.equals(p_177241_)) {
                    throw new GameTestAssertException("Expected entity data to be: " + p_177241_ + ", but was: " + $$7);
                }
            }

        }
    }

    public <E extends LivingEntity> void assertEntityIsHolding(BlockPos p_263501_, EntityType<E> p_263510_, Item p_263517_) {
        BlockPos $$3 = this.absolutePos(p_263501_);
        List<E> $$4 = this.getLevel().getEntities(p_263510_, new AABB($$3), Entity::isAlive);
        if ($$4.isEmpty()) {
            throw new GameTestAssertPosException("Expected entity of type: " + p_263510_, $$3, p_263501_, this.getTick());
        } else {
            Iterator var6 = $$4.iterator();

            LivingEntity $$5;
            do {
                if (!var6.hasNext()) {
                    throw new GameTestAssertPosException("Entity should be holding: " + p_263517_, $$3, p_263501_, this.getTick());
                }

                $$5 = (LivingEntity)var6.next();
            } while(!$$5.isHolding(p_263517_));

        }
    }

    public <E extends Entity & InventoryCarrier> void assertEntityInventoryContains(BlockPos p_263495_, EntityType<E> p_263521_, Item p_263502_) {
        BlockPos $$3 = this.absolutePos(p_263495_);
        List<E> $$4 = this.getLevel().getEntities(p_263521_, new AABB($$3), (p_263479_) -> {
            return ((Entity)p_263479_).isAlive();
        });
        if ($$4.isEmpty()) {
            throw new GameTestAssertPosException("Expected " + p_263521_.toShortString() + " to exist", $$3, p_263495_, this.getTick());
        } else {
            Iterator var6 = $$4.iterator();

            Entity $$5;
            do {
                if (!var6.hasNext()) {
                    throw new GameTestAssertPosException("Entity inventory should contain: " + p_263502_, $$3, p_263495_, this.getTick());
                }

                $$5 = (Entity)var6.next();
            } while(!((InventoryCarrier)$$5).getInventory().hasAnyMatching((p_263481_) -> {
                return p_263481_.is(p_263502_);
            }));

        }
    }

    public void assertContainerEmpty(BlockPos p_177441_) {
        BlockPos $$1 = this.absolutePos(p_177441_);
        BlockEntity $$2 = this.getLevel().getBlockEntity($$1);
        if ($$2 instanceof BaseContainerBlockEntity && !((BaseContainerBlockEntity)$$2).isEmpty()) {
            throw new GameTestAssertException("Container should be empty");
        }
    }

    public void assertContainerContains(BlockPos p_177243_, Item p_177244_) {
        BlockPos $$2 = this.absolutePos(p_177243_);
        BlockEntity $$3 = this.getLevel().getBlockEntity($$2);
        if (!($$3 instanceof BaseContainerBlockEntity)) {
            throw new GameTestAssertException("Expected a container at " + p_177243_ + ", found " + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey($$3.getType()));
        } else if (((BaseContainerBlockEntity)$$3).countItem(p_177244_) != 1) {
            throw new GameTestAssertException("Container should contain: " + p_177244_);
        }
    }

    public void assertSameBlockStates(BoundingBox p_177225_, BlockPos p_177226_) {
        BlockPos.betweenClosedStream(p_177225_).forEach((p_177267_) -> {
            BlockPos $$3 = p_177226_.offset(p_177267_.getX() - p_177225_.minX(), p_177267_.getY() - p_177225_.minY(), p_177267_.getZ() - p_177225_.minZ());
            this.assertSameBlockState(p_177267_, $$3);
        });
    }

    public void assertSameBlockState(BlockPos p_177269_, BlockPos p_177270_) {
        BlockState $$2 = this.getBlockState(p_177269_);
        BlockState $$3 = this.getBlockState(p_177270_);
        if ($$2 != $$3) {
            this.fail("Incorrect state. Expected " + $$3 + ", got " + $$2, p_177269_);
        }

    }

    public void assertAtTickTimeContainerContains(long p_177124_, BlockPos p_177125_, Item p_177126_) {
        this.runAtTickTime(p_177124_, () -> {
            this.assertContainerContains(p_177125_, p_177126_);
        });
    }

    public void assertAtTickTimeContainerEmpty(long p_177121_, BlockPos p_177122_) {
        this.runAtTickTime(p_177121_, () -> {
            this.assertContainerEmpty(p_177122_);
        });
    }

    public <E extends Entity, T> void succeedWhenEntityData(BlockPos p_177350_, EntityType<E> p_177351_, Function<E, T> p_177352_, T p_177353_) {
        this.succeedWhen(() -> {
            this.assertEntityData(p_177350_, p_177351_, p_177352_, p_177353_);
        });
    }

    public <E extends Entity> void assertEntityProperty(E p_177153_, Predicate<E> p_177154_, String p_177155_) {
        if (!p_177154_.test(p_177153_)) {
            throw new GameTestAssertException("Entity " + p_177153_ + " failed " + p_177155_ + " test");
        }
    }

    public <E extends Entity, T> void assertEntityProperty(E p_177148_, Function<E, T> p_177149_, String p_177150_, T p_177151_) {
        T $$4 = p_177149_.apply(p_177148_);
        if (!$$4.equals(p_177151_)) {
            throw new GameTestAssertException("Entity " + p_177148_ + " value " + p_177150_ + "=" + $$4 + " is not equal to expected " + p_177151_);
        }
    }

    public void succeedWhenEntityPresent(EntityType<?> p_177414_, int p_177415_, int p_177416_, int p_177417_) {
        this.succeedWhenEntityPresent(p_177414_, new BlockPos(p_177415_, p_177416_, p_177417_));
    }

    public void succeedWhenEntityPresent(EntityType<?> p_177419_, BlockPos p_177420_) {
        this.succeedWhen(() -> {
            this.assertEntityPresent(p_177419_, p_177420_);
        });
    }

    public void succeedWhenEntityNotPresent(EntityType<?> p_177427_, int p_177428_, int p_177429_, int p_177430_) {
        this.succeedWhenEntityNotPresent(p_177427_, new BlockPos(p_177428_, p_177429_, p_177430_));
    }

    public void succeedWhenEntityNotPresent(EntityType<?> p_177432_, BlockPos p_177433_) {
        this.succeedWhen(() -> {
            this.assertEntityNotPresent(p_177432_, p_177433_);
        });
    }

    public void succeed() {
        this.testInfo.succeed();
    }

    private void ensureSingleFinalCheck() {
        if (this.finalCheckAdded) {
            throw new IllegalStateException("This test already has final clause");
        } else {
            this.finalCheckAdded = true;
        }
    }

    public void succeedIf(Runnable p_177280_) {
        this.ensureSingleFinalCheck();
        this.testInfo.createSequence().thenWaitUntil(0L, p_177280_).thenSucceed();
    }

    public void succeedWhen(Runnable p_177362_) {
        this.ensureSingleFinalCheck();
        this.testInfo.createSequence().thenWaitUntil(p_177362_).thenSucceed();
    }

    public void succeedOnTickWhen(int p_177118_, Runnable p_177119_) {
        this.ensureSingleFinalCheck();
        this.testInfo.createSequence().thenWaitUntil((long)p_177118_, p_177119_).thenSucceed();
    }

    public void runAtTickTime(long p_177128_, Runnable p_177129_) {
        this.testInfo.setRunAtTickTime(p_177128_, p_177129_);
    }

    public void runAfterDelay(long p_177307_, Runnable p_177308_) {
        this.runAtTickTime(this.testInfo.getTick() + p_177307_, p_177308_);
    }

    public void randomTick(BlockPos p_177447_) {
        BlockPos $$1 = this.absolutePos(p_177447_);
        ServerLevel $$2 = this.getLevel();
        $$2.getBlockState($$1).randomTick($$2, $$1, $$2.random);
    }

    public int getHeight(Heightmap.Types p_236775_, int p_236776_, int p_236777_) {
        BlockPos $$3 = this.absolutePos(new BlockPos(p_236776_, 0, p_236777_));
        return this.relativePos(this.getLevel().getHeightmapPos(p_236775_, $$3)).getY();
    }

    public void fail(String p_177290_, BlockPos p_177291_) {
        throw new GameTestAssertPosException(p_177290_, this.absolutePos(p_177291_), p_177291_, this.getTick());
    }

    public void fail(String p_177287_, Entity p_177288_) {
        throw new GameTestAssertPosException(p_177287_, p_177288_.blockPosition(), this.relativePos(p_177288_.blockPosition()), this.getTick());
    }

    public void fail(String p_177285_) {
        throw new GameTestAssertException(p_177285_);
    }

    public void failIf(Runnable p_177393_) {
        this.testInfo.createSequence().thenWaitUntil(p_177393_).thenFail(() -> {
            return new GameTestAssertException("Fail conditions met");
        });
    }

    public void failIfEver(Runnable p_177411_) {
        LongStream.range(this.testInfo.getTick(), (long)this.testInfo.getTimeoutTicks()).forEach((p_177365_) -> {
            GameTestInfo var10000 = this.testInfo;
            Objects.requireNonNull(p_177411_);
            var10000.setRunAtTickTime(p_177365_, p_177411_::run);
        });
    }

    public GameTestSequence startSequence() {
        return this.testInfo.createSequence();
    }

    public BlockPos absolutePos(BlockPos p_177450_) {
        BlockPos $$1 = this.testInfo.getStructureBlockPos();
        BlockPos $$2 = $$1.offset(p_177450_);
        return StructureTemplate.transform($$2, Mirror.NONE, this.testInfo.getRotation(), $$1);
    }

    public BlockPos relativePos(BlockPos p_177453_) {
        BlockPos $$1 = this.testInfo.getStructureBlockPos();
        Rotation $$2 = this.testInfo.getRotation().getRotated(Rotation.CLOCKWISE_180);
        BlockPos $$3 = StructureTemplate.transform(p_177453_, Mirror.NONE, $$2, $$1);
        return $$3.subtract($$1);
    }

    public Vec3 absoluteVec(Vec3 p_177228_) {
        Vec3 $$1 = Vec3.atLowerCornerOf(this.testInfo.getStructureBlockPos());
        return StructureTemplate.transform($$1.add(p_177228_), Mirror.NONE, this.testInfo.getRotation(), this.testInfo.getStructureBlockPos());
    }

    public Vec3 relativeVec(Vec3 p_251543_) {
        Vec3 $$1 = Vec3.atLowerCornerOf(this.testInfo.getStructureBlockPos());
        return StructureTemplate.transform(p_251543_.subtract($$1), Mirror.NONE, this.testInfo.getRotation(), this.testInfo.getStructureBlockPos());
    }

    public void assertTrue(boolean p_249380_, String p_248720_) {
        if (!p_249380_) {
            throw new GameTestAssertException(p_248720_);
        }
    }

    public void assertFalse(boolean p_277974_, String p_277933_) {
        if (p_277974_) {
            throw new GameTestAssertException(p_277933_);
        }
    }

    public long getTick() {
        return this.testInfo.getTick();
    }

    private AABB getBounds() {
        return this.testInfo.getStructureBounds();
    }

    private AABB getRelativeBounds() {
        AABB $$0 = this.testInfo.getStructureBounds();
        return $$0.move(BlockPos.ZERO.subtract(this.absolutePos(BlockPos.ZERO)));
    }

    public void forEveryBlockInStructure(Consumer<BlockPos> p_177293_) {
        AABB $$1 = this.getRelativeBounds();
        MutableBlockPos.betweenClosedStream($$1.move(0.0, 1.0, 0.0)).forEach(p_177293_);
    }

    public void onEachTick(Runnable p_177424_) {
        LongStream.range(this.testInfo.getTick(), (long)this.testInfo.getTimeoutTicks()).forEach((p_177283_) -> {
            GameTestInfo var10000 = this.testInfo;
            Objects.requireNonNull(p_177424_);
            var10000.setRunAtTickTime(p_177283_, p_177424_::run);
        });
    }

    public void placeAt(Player p_261595_, ItemStack p_262007_, BlockPos p_261973_, Direction p_262008_) {
        BlockPos $$4 = this.absolutePos(p_261973_.relative(p_262008_));
        BlockHitResult $$5 = new BlockHitResult(Vec3.atCenterOf($$4), p_262008_, $$4, false);
        UseOnContext $$6 = new UseOnContext(p_261595_, InteractionHand.MAIN_HAND, $$5);
        p_262007_.useOn($$6);
    }
}
