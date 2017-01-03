/*
 * Copyright (C) 2017 The MoonLake Authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.minecraft.moonlake.script.listener;

import com.minecraft.moonlake.api.event.MoonLakeListener;
import com.minecraft.moonlake.script.execute.Executor;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public class EventListener<E extends Executor<E>> implements MoonLakeListener, EventExecutor, Comparable<EventListener<E>> {

    private final E executor;
    private final Class<? extends Event> event;

    public EventListener(E executor, Class<? extends Event> event) {
        this.executor = executor;
        this.event = event;
    }

    public E getExecutor() {
        return executor;
    }

    public Class<? extends Event> getEvent() {
        return event;
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        try {
            if(event.getClass() == this.event)
                executor.execute(event);
        } catch (Exception e) {
            throw new EventException(e);
        }
    }

    @Override
    public int compareTo(EventListener<E> o) {
        int compareFirst = event.hashCode() - o.event.hashCode();
        if(compareFirst != 0) return compareFirst;

        int compareSecond = executor.getClass().hashCode() - o.executor.getClass().hashCode();
        if(compareSecond != 0) return compareSecond;
        return executor.compareTo(o.executor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventListener<?> that = (EventListener<?>) o;

        if (!executor.equals(that.executor)) return false;
        return event.equals(that.event);
    }

    @Override
    public int hashCode() {
        int result = executor.hashCode();
        result = 31 * result + event.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "EventListener{" +
                "executor=" + executor +
                ", listener=" + event +
                '}';
    }
}
