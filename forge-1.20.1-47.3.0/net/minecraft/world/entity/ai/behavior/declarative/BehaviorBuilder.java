//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior.declarative;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Unit;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BehaviorBuilder<E extends LivingEntity, M> implements App<Mu<E>, M> {
    private final TriggerWithResult<E, M> trigger;

    public static <E extends LivingEntity, M> BehaviorBuilder<E, M> unbox(App<Mu<E>, M> p_259593_) {
        return (BehaviorBuilder)p_259593_;
    }

    public static <E extends LivingEntity> Instance<E> instance() {
        return new Instance();
    }

    public static <E extends LivingEntity> OneShot<E> create(Function<Instance<E>, ? extends App<Mu<E>, Trigger<E>>> p_259386_) {
        final TriggerWithResult<E, Trigger<E>> $$1 = get((App)p_259386_.apply(instance()));
        return new OneShot<E>() {
            public boolean trigger(ServerLevel p_259385_, E p_260003_, long p_259194_) {
                Trigger<E> $$3 = (Trigger)$$1.tryTrigger(p_259385_, p_260003_, p_259194_);
                return $$3 == null ? false : $$3.trigger(p_259385_, p_260003_, p_259194_);
            }

            public String debugString() {
                return "OneShot[" + $$1.debugString() + "]";
            }

            public String toString() {
                return this.debugString();
            }
        };
    }

    public static <E extends LivingEntity> OneShot<E> sequence(Trigger<? super E> p_260174_, Trigger<? super E> p_259134_) {
        return create((p_259495_) -> {
            return p_259495_.group(p_259495_.ifTriggered(p_260174_)).apply(p_259495_, (p_260322_) -> {
                Objects.requireNonNull(p_259134_);
                return p_259134_::trigger;
            });
        });
    }

    public static <E extends LivingEntity> OneShot<E> triggerIf(Predicate<E> p_260059_, OneShot<? super E> p_259640_) {
        return sequence(triggerIf(p_260059_), p_259640_);
    }

    public static <E extends LivingEntity> OneShot<E> triggerIf(Predicate<E> p_260112_) {
        return create((p_260353_) -> {
            return p_260353_.point((p_259280_, p_259428_, p_259845_) -> {
                return p_260112_.test(p_259428_);
            });
        });
    }

    public static <E extends LivingEntity> OneShot<E> triggerIf(BiPredicate<ServerLevel, E> p_259227_) {
        return create((p_260191_) -> {
            return p_260191_.point((p_259079_, p_259093_, p_260140_) -> {
                return p_259227_.test(p_259079_, p_259093_);
            });
        });
    }

    static <E extends LivingEntity, M> TriggerWithResult<E, M> get(App<Mu<E>, M> p_259615_) {
        return unbox(p_259615_).trigger;
    }

    BehaviorBuilder(TriggerWithResult<E, M> p_260164_) {
        this.trigger = p_260164_;
    }

    static <E extends LivingEntity, M> BehaviorBuilder<E, M> create(TriggerWithResult<E, M> p_259575_) {
        return new BehaviorBuilder(p_259575_);
    }

    public static final class Instance<E extends LivingEntity> implements Applicative<Mu<E>, Mu<E>> {
        public Instance() {
        }

        public <Value> Optional<Value> tryGet(MemoryAccessor<OptionalBox.Mu, Value> p_259352_) {
            return OptionalBox.unbox(p_259352_.value());
        }

        public <Value> Value get(MemoryAccessor<IdF.Mu, Value> p_259206_) {
            return IdF.get(p_259206_.value());
        }

        public <Value> BehaviorBuilder<E, MemoryAccessor<OptionalBox.Mu, Value>> registered(MemoryModuleType<Value> p_259477_) {
            return new PureMemory(new MemoryCondition.Registered(p_259477_));
        }

        public <Value> BehaviorBuilder<E, MemoryAccessor<IdF.Mu, Value>> present(MemoryModuleType<Value> p_259673_) {
            return new PureMemory(new MemoryCondition.Present(p_259673_));
        }

        public <Value> BehaviorBuilder<E, MemoryAccessor<Const.Mu<Unit>, Value>> absent(MemoryModuleType<Value> p_260198_) {
            return new PureMemory(new MemoryCondition.Absent(p_260198_));
        }

        public BehaviorBuilder<E, Unit> ifTriggered(Trigger<? super E> p_260247_) {
            return new TriggerWrapper(p_260247_);
        }

        public <A> BehaviorBuilder<E, A> point(A p_259634_) {
            return new Constant(p_259634_);
        }

        public <A> BehaviorBuilder<E, A> point(Supplier<String> p_260070_, A p_260295_) {
            return new Constant(p_260295_, p_260070_);
        }

        public <A, R> Function<App<Mu<E>, A>, App<Mu<E>, R>> lift1(App<Mu<E>, Function<A, R>> p_259294_) {
            return (p_259751_) -> {
                final TriggerWithResult<E, A> $$2 = BehaviorBuilder.get(p_259751_);
                final TriggerWithResult<E, Function<A, R>> $$3 = BehaviorBuilder.get(p_259294_);
                return BehaviorBuilder.create(new TriggerWithResult<E, R>() {
                    public R tryTrigger(ServerLevel p_259603_, E p_260233_, long p_259654_) {
                        A $$3x = $$2.tryTrigger(p_259603_, p_260233_, p_259654_);
                        if ($$3x == null) {
                            return null;
                        } else {
                            Function<A, R> $$4 = (Function)$$3.tryTrigger(p_259603_, p_260233_, p_259654_);
                            return $$4 == null ? null : $$4.apply($$3x);
                        }
                    }

                    public String debugString() {
                        String var10000 = $$3.debugString();
                        return var10000 + " * " + $$2.debugString();
                    }

                    public String toString() {
                        return this.debugString();
                    }
                });
            };
        }

        public <T, R> BehaviorBuilder<E, R> map(final Function<? super T, ? extends R> p_259963_, App<Mu<E>, T> p_260355_) {
            final TriggerWithResult<E, T> $$2 = BehaviorBuilder.get(p_260355_);
            return BehaviorBuilder.create(new TriggerWithResult<E, R>() {
                public R tryTrigger(ServerLevel p_259755_, E p_259656_, long p_259300_) {
                    T $$3 = $$2.tryTrigger(p_259755_, p_259656_, p_259300_);
                    return $$3 == null ? null : p_259963_.apply($$3);
                }

                public String debugString() {
                    String var10000 = $$2.debugString();
                    return var10000 + ".map[" + p_259963_ + "]";
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }

        public <A, B, R> BehaviorBuilder<E, R> ap2(App<Mu<E>, BiFunction<A, B, R>> p_259535_, App<Mu<E>, A> p_259162_, App<Mu<E>, B> p_259733_) {
            final TriggerWithResult<E, A> $$3 = BehaviorBuilder.get(p_259162_);
            final TriggerWithResult<E, B> $$4 = BehaviorBuilder.get(p_259733_);
            final TriggerWithResult<E, BiFunction<A, B, R>> $$5 = BehaviorBuilder.get(p_259535_);
            return BehaviorBuilder.create(new TriggerWithResult<E, R>() {
                public R tryTrigger(ServerLevel p_259274_, E p_259817_, long p_259820_) {
                    A $$3x = $$3.tryTrigger(p_259274_, p_259817_, p_259820_);
                    if ($$3x == null) {
                        return null;
                    } else {
                        B $$4x = $$4.tryTrigger(p_259274_, p_259817_, p_259820_);
                        if ($$4x == null) {
                            return null;
                        } else {
                            BiFunction<A, B, R> $$5x = (BiFunction)$$5.tryTrigger(p_259274_, p_259817_, p_259820_);
                            return $$5x == null ? null : $$5x.apply($$3x, $$4x);
                        }
                    }
                }

                public String debugString() {
                    String var10000 = $$5.debugString();
                    return var10000 + " * " + $$3.debugString() + " * " + $$4.debugString();
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }

        public <T1, T2, T3, R> BehaviorBuilder<E, R> ap3(App<Mu<E>, Function3<T1, T2, T3, R>> p_260239_, App<Mu<E>, T1> p_259239_, App<Mu<E>, T2> p_259638_, App<Mu<E>, T3> p_259969_) {
            final TriggerWithResult<E, T1> $$4 = BehaviorBuilder.get(p_259239_);
            final TriggerWithResult<E, T2> $$5 = BehaviorBuilder.get(p_259638_);
            final TriggerWithResult<E, T3> $$6 = BehaviorBuilder.get(p_259969_);
            final TriggerWithResult<E, Function3<T1, T2, T3, R>> $$7 = BehaviorBuilder.get(p_260239_);
            return BehaviorBuilder.create(new TriggerWithResult<E, R>() {
                public R tryTrigger(ServerLevel p_259096_, E p_260221_, long p_259035_) {
                    T1 $$3 = $$4.tryTrigger(p_259096_, p_260221_, p_259035_);
                    if ($$3 == null) {
                        return null;
                    } else {
                        T2 $$4x = $$5.tryTrigger(p_259096_, p_260221_, p_259035_);
                        if ($$4x == null) {
                            return null;
                        } else {
                            T3 $$5x = $$6.tryTrigger(p_259096_, p_260221_, p_259035_);
                            if ($$5x == null) {
                                return null;
                            } else {
                                Function3<T1, T2, T3, R> $$6x = (Function3)$$7.tryTrigger(p_259096_, p_260221_, p_259035_);
                                return $$6x == null ? null : $$6x.apply($$3, $$4x, $$5x);
                            }
                        }
                    }
                }

                public String debugString() {
                    String var10000 = $$7.debugString();
                    return var10000 + " * " + $$4.debugString() + " * " + $$5.debugString() + " * " + $$6.debugString();
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }

        public <T1, T2, T3, T4, R> BehaviorBuilder<E, R> ap4(App<Mu<E>, Function4<T1, T2, T3, T4, R>> p_259519_, App<Mu<E>, T1> p_259829_, App<Mu<E>, T2> p_259314_, App<Mu<E>, T3> p_260089_, App<Mu<E>, T4> p_259136_) {
            final TriggerWithResult<E, T1> $$5 = BehaviorBuilder.get(p_259829_);
            final TriggerWithResult<E, T2> $$6 = BehaviorBuilder.get(p_259314_);
            final TriggerWithResult<E, T3> $$7 = BehaviorBuilder.get(p_260089_);
            final TriggerWithResult<E, T4> $$8 = BehaviorBuilder.get(p_259136_);
            final TriggerWithResult<E, Function4<T1, T2, T3, T4, R>> $$9 = BehaviorBuilder.get(p_259519_);
            return BehaviorBuilder.create(new TriggerWithResult<E, R>() {
                public R tryTrigger(ServerLevel p_259537_, E p_259581_, long p_259423_) {
                    T1 $$3 = $$5.tryTrigger(p_259537_, p_259581_, p_259423_);
                    if ($$3 == null) {
                        return null;
                    } else {
                        T2 $$4 = $$6.tryTrigger(p_259537_, p_259581_, p_259423_);
                        if ($$4 == null) {
                            return null;
                        } else {
                            T3 $$5x = $$7.tryTrigger(p_259537_, p_259581_, p_259423_);
                            if ($$5x == null) {
                                return null;
                            } else {
                                T4 $$6x = $$8.tryTrigger(p_259537_, p_259581_, p_259423_);
                                if ($$6x == null) {
                                    return null;
                                } else {
                                    Function4<T1, T2, T3, T4, R> $$7x = (Function4)$$9.tryTrigger(p_259537_, p_259581_, p_259423_);
                                    return $$7x == null ? null : $$7x.apply($$3, $$4, $$5x, $$6x);
                                }
                            }
                        }
                    }
                }

                public String debugString() {
                    String var10000 = $$9.debugString();
                    return var10000 + " * " + $$5.debugString() + " * " + $$6.debugString() + " * " + $$7.debugString() + " * " + $$8.debugString();
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }

        private static final class Mu<E extends LivingEntity> implements Applicative.Mu {
            private Mu() {
            }
        }
    }

    private interface TriggerWithResult<E extends LivingEntity, R> {
        @Nullable
        R tryTrigger(ServerLevel var1, E var2, long var3);

        String debugString();
    }

    private static final class TriggerWrapper<E extends LivingEntity> extends BehaviorBuilder<E, Unit> {
        TriggerWrapper(final Trigger<? super E> p_259310_) {
            super(new TriggerWithResult<E, Unit>() {
                @Nullable
                public Unit tryTrigger(ServerLevel p_259397_, E p_260169_, long p_259155_) {
                    return p_259310_.trigger(p_259397_, p_260169_, p_259155_) ? Unit.INSTANCE : null;
                }

                public String debugString() {
                    return "T[" + p_259310_ + "]";
                }
            });
        }
    }

    private static final class Constant<E extends LivingEntity, A> extends BehaviorBuilder<E, A> {
        Constant(A p_259906_) {
            this(p_259906_, () -> {
                return "C[" + p_259906_ + "]";
            });
        }

        Constant(final A p_259514_, final Supplier<String> p_259950_) {
            super(new TriggerWithResult<E, A>() {
                public A tryTrigger(ServerLevel p_259561_, E p_259467_, long p_259297_) {
                    return p_259514_;
                }

                public String debugString() {
                    return (String)p_259950_.get();
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }
    }

    private static final class PureMemory<E extends LivingEntity, F extends K1, Value> extends BehaviorBuilder<E, MemoryAccessor<F, Value>> {
        PureMemory(final MemoryCondition<F, Value> p_259776_) {
            super(new TriggerWithResult<E, MemoryAccessor<F, Value>>() {
                public MemoryAccessor<F, Value> tryTrigger(ServerLevel p_259899_, E p_259558_, long p_259793_) {
                    Brain<?> $$3 = p_259558_.getBrain();
                    Optional<Value> $$4 = $$3.getMemoryInternal(p_259776_.memory());
                    return $$4 == null ? null : p_259776_.createAccessor($$3, $$4);
                }

                public String debugString() {
                    return "M[" + p_259776_ + "]";
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }
    }

    public static final class Mu<E extends LivingEntity> implements K1 {
        public Mu() {
        }
    }
}
