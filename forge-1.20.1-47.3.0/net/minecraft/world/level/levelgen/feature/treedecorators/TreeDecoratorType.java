//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class TreeDecoratorType<P extends TreeDecorator> {
    public static final TreeDecoratorType<TrunkVineDecorator> TRUNK_VINE;
    public static final TreeDecoratorType<LeaveVineDecorator> LEAVE_VINE;
    public static final TreeDecoratorType<CocoaDecorator> COCOA;
    public static final TreeDecoratorType<BeehiveDecorator> BEEHIVE;
    public static final TreeDecoratorType<AlterGroundDecorator> ALTER_GROUND;
    public static final TreeDecoratorType<AttachedToLeavesDecorator> ATTACHED_TO_LEAVES;
    private final Codec<P> codec;

    private static <P extends TreeDecorator> TreeDecoratorType<P> register(String p_70053_, Codec<P> p_70054_) {
        return (TreeDecoratorType)Registry.register(BuiltInRegistries.TREE_DECORATOR_TYPE, (String)p_70053_, new TreeDecoratorType(p_70054_));
    }

    public TreeDecoratorType(Codec<P> p_70050_) {
        this.codec = p_70050_;
    }

    public Codec<P> codec() {
        return this.codec;
    }

    static {
        TRUNK_VINE = register("trunk_vine", TrunkVineDecorator.CODEC);
        LEAVE_VINE = register("leave_vine", LeaveVineDecorator.CODEC);
        COCOA = register("cocoa", CocoaDecorator.CODEC);
        BEEHIVE = register("beehive", BeehiveDecorator.CODEC);
        ALTER_GROUND = register("alter_ground", AlterGroundDecorator.CODEC);
        ATTACHED_TO_LEAVES = register("attached_to_leaves", AttachedToLeavesDecorator.CODEC);
    }
}
