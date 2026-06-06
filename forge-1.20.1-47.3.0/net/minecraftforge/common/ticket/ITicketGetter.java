//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.ticket;

import java.util.Collection;

public interface ITicketGetter<T> extends ITicketManager<T> {
    Collection<SimpleTicket<T>> getTickets();
}
