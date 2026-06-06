//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.server.permission.events;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.server.permission.handler.DefaultPermissionHandler;
import net.minecraftforge.server.permission.handler.IPermissionHandlerFactory;
import net.minecraftforge.server.permission.nodes.PermissionNode;

public class PermissionGatherEvent extends Event {
    public PermissionGatherEvent() {
    }

    public static class Nodes extends PermissionGatherEvent {
        private final Set<PermissionNode<?>> nodes = new HashSet();

        public Nodes() {
        }

        public Collection<PermissionNode<?>> getNodes() {
            return Collections.unmodifiableCollection(this.nodes);
        }

        public void addNodes(PermissionNode<?>... nodes) {
            PermissionNode[] var2 = nodes;
            int var3 = nodes.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                PermissionNode<?> node = var2[var4];
                if (!this.nodes.add(node)) {
                    throw new IllegalArgumentException("Tried to register duplicate PermissionNode '" + node.getNodeName() + "'");
                }
            }

        }

        public void addNodes(Iterable<PermissionNode<?>> nodes) {
            Iterator var2 = nodes.iterator();

            PermissionNode node;
            do {
                if (!var2.hasNext()) {
                    return;
                }

                node = (PermissionNode)var2.next();
            } while(this.nodes.add(node));

            throw new IllegalArgumentException("Tried to register duplicate PermissionNode '" + node.getNodeName() + "'");
        }
    }

    public static class Handler extends PermissionGatherEvent {
        private Map<ResourceLocation, IPermissionHandlerFactory> availableHandlers = new HashMap();

        public Handler() {
            this.availableHandlers.put(DefaultPermissionHandler.IDENTIFIER, DefaultPermissionHandler::new);
        }

        public Map<ResourceLocation, IPermissionHandlerFactory> getAvailablePermissionHandlerFactories() {
            return Collections.unmodifiableMap(this.availableHandlers);
        }

        public void addPermissionHandler(ResourceLocation identifier, IPermissionHandlerFactory handlerFactory) {
            Preconditions.checkNotNull(identifier, "Permission handler identifier cannot be null!");
            Preconditions.checkNotNull(handlerFactory, "Permission handler cannot be null!");
            if (this.availableHandlers.containsKey(identifier)) {
                throw new IllegalArgumentException("Attempted to overwrite permission handler " + identifier + ", this is not allowed.");
            } else {
                this.availableHandlers.put(identifier, handlerFactory);
            }
        }
    }
}
