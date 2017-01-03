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

public class ExecutorFunction implements Executor<ExecutorFunction> {

    private final Invocable script;
    private final String function;

    public ExecutorFunction(Invocable script, String function) {
        this.script = script;
        this.function = function;
    }

    public Invocable getScript() {
        return script;
    }

    public String getFunction() {
        return function;
    }

    @Override
    public Object execute(Object... argument) throws Exception {
        return script.invokeFunction(function, argument);
    }

    @Override
    public int compareTo(ExecutorFunction o) {
        int compareFirst = script.hashCode() - o.script.hashCode();
        if(compareFirst != 0) return compareFirst;
        return function.compareTo(o.function);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExecutorFunction that = (ExecutorFunction) o;

        if (!script.equals(that.script)) return false;
        return function.equals(that.function);
    }

    @Override
    public int hashCode() {
        int result = script.hashCode();
        result = 31 * result + function.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ExecutorFunction{" +
                "script=" + script +
                ", function='" + function + '\'' +
                '}';
    }
}
