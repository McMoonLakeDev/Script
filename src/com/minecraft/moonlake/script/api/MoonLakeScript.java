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


package com.minecraft.moonlake.script.api;

import com.minecraft.moonlake.MoonLakeAPI;
import com.minecraft.moonlake.script.ScriptPlugin;
import com.minecraft.moonlake.script.execute.ExecutorFunction;
import com.minecraft.moonlake.script.execute.ExecutorMethod;
import com.minecraft.moonlake.script.listener.EventListener;
import com.minecraft.moonlake.script.listener.EventMapping;
import com.minecraft.moonlake.validate.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MoonLakeScript {

    final ScriptPlugin parent;
    final ScriptEngine engine;
    final String name;

    public MoonLakeScript(ScriptPlugin parent, String name, ScriptEngine engine) {
        this.parent = parent;
        this.engine = engine;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Logger getLogger() {
        return parent.getLogger();
    }

    /** 卸载钩子函数处理区域 */
    private Runnable unloadHook;

    public void setUnloadHook(Runnable unloadHook) {
        // 设置卸载钩子函数
        this.unloadHook = unloadHook;
    }

    public void unload() {
        // 卸载函数: 调用后脚本将会被完全卸载
        if(unloadHook != null) {
            // 不为 null 则调用 unloadHook 函数
            try {
                unloadHook.run();
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "The run unload hook error exception", e);
            }
        }
        // 卸载所有的事件监听器
        functionSet.forEach(this::unregisterListener);
        functionSet.clear();
        methodSet.forEach(this::unregisterListener);
        methodSet.clear();
    }

    /** 事件监听器处理区域 */
    private final Set<EventListener<ExecutorFunction>> functionSet = new TreeSet<>();
    private final Set<EventListener<ExecutorMethod>> methodSet = new TreeSet<>();

    protected HandlerList getHandlerList(Class<? extends Event> event) {
        // 获取指定事件类的处理器列表对象
        return EventMapping.getHandler(event);
    }

    protected boolean unregisterListener(EventListener<?> listener) {
        // 将指定事件监听器卸载
        return unregisterListener(listener.getEvent(), listener);
    }

    protected boolean unregisterListener(Class<? extends Event> event, EventListener<?> listener) {
        // 将指定事件类从监听器卸载
        HandlerList handlerList = getHandlerList(event);
        if(handlerList == null)
            // 为 null 则卸载失败
            return false;
        // 否则调用处理器列表进行卸载
        handlerList.unregister(listener);
        return true;
    }

    public boolean registerListener(String name, EventPriority priority, boolean ignoreCancelled, Class<? extends Event> event) {
        // 注册事件监听器: Function
        ExecutorFunction function = new ExecutorFunction((Invocable) engine, name);
        EventListener<ExecutorFunction> functionListener = new EventListener<>(function, event);

        if(functionSet.add(functionListener)) {
            // add 到集合成功则注册事件
            MoonLakeAPI.registerEvent(event, functionListener, priority, functionListener, parent, ignoreCancelled);
            return true;
        }
        return false;
    }

    public boolean unregisterListener(String name, Class<? extends Event> event) {
        // 卸载事件监听器: Function
        ExecutorFunction function = new ExecutorFunction((Invocable) engine, name);
        EventListener<ExecutorFunction> functionListener = new EventListener<>(function, event);
        // remove 从集合成功则卸载事件
        return functionSet.remove(functionListener) && unregisterListener(event, functionListener);
    }

    public boolean registerListener(Object instance, String name, EventPriority priority, boolean ignoreCancelled, Class<? extends Event> event) {
        // 注册事件监听器: Method
        ExecutorMethod method = new ExecutorMethod((Invocable) engine, instance, name);
        EventListener<ExecutorMethod> methodListener = new EventListener<>(method, event);

        if(methodSet.add(methodListener)) {
            // add 到集合成功则注册事件
            MoonLakeAPI.registerEvent(event, methodListener, priority, methodListener, parent, ignoreCancelled);
            return true;
        }
        return false;
    }

    public boolean unregisterListener(Object instance, String name, Class<? extends Event> event) {
        // 卸载事件监听器: Method
        ExecutorMethod method = new ExecutorMethod((Invocable) engine, instance, name);
        EventListener<ExecutorMethod> methodListener = new EventListener<>(method, event);
        // remove 从集合成功则卸载事件
        return methodSet.remove(methodListener) && unregisterListener(event, methodListener);
    }

    public boolean registerListener(Map<String, Object> listener) {
        // 注册事件监听器: Map
        // 必须拥有的属性: listener -> 事件名称
        //                           handler -> 处理器函数
        Validate.isTrue(listener.containsKey("event"), "The listener not has event.");
        Validate.isTrue(listener.containsKey("handler"), "The listener not has event handler.");

        Class<? extends Event> event = EventMapping.INSTANCE.getEventClass(listener.get("event").toString());
        // 验证事件类是否存在
        Validate.notNull(event, "The listener event class not mapping or not exists.");
        // 存在则进行加载
        EventPriority priority = (EventPriority) listener.getOrDefault("priority", EventPriority.NORMAL);
        boolean ignoreCancelled = (boolean) listener.getOrDefault("ignoreCancelled", false);
        // 注册事件监听器
        return registerListener(listener, "handler", priority, ignoreCancelled, event);
    }

    /** TODO 命令处理区域 */

    /** TODO 任务处理区域 */

    /** TODO 其他处理区域 */
}
