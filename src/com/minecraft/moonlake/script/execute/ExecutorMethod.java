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


package com.minecraft.moonlake.script.execute;

import javax.script.Invocable;

public class ExecutorMethod implements Executor<ExecutorMethod> {

    private final Invocable script;
    private final Object instance;
    private final String method;

    public ExecutorMethod(Invocable script, Object instance, String method) {
        this.script = script;
        this.instance = instance;
        this.method = method;
    }

    public Invocable getScript() {
        return script;
    }

    public Object getInstance() {
        return instance;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public Object execute(Object... argument) throws Exception {
        return script.invokeMethod(instance, method, argument);
    }

    @Override
    public int compareTo(ExecutorMethod o) {
        int compareFirst = script.hashCode() - o.script.hashCode();
        if(compareFirst != 0) return compareFirst;

        int compareSecond = instance.hashCode() - o.instance.hashCode();
        if(compareSecond != 0) return compareSecond;
        return method.compareTo(o.method);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExecutorMethod that = (ExecutorMethod) o;

        if (!script.equals(that.script)) return false;
        if (!instance.equals(that.instance)) return false;
        return method.equals(that.method);
    }

    @Override
    public int hashCode() {
        int result = script.hashCode();
        result = 31 * result + instance.hashCode();
        result = 31 * result + method.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ExecutorMethod{" +
                "script=" + script +
                ", instance=" + instance +
                ", method='" + method + '\'' +
                '}';
    }
}
