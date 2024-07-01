package implementslegendkt.vhpuzzles.block;

import implementslegendkt.vhpuzzles.puzzles.PuzzleLogic;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record NewPuzzleType(Class<? extends PuzzleLogic> type) implements StringRepresentable, Comparable<NewPuzzleType> {
    public static final List<NewPuzzleType> VALUES = Arrays.stream(((Class<? extends PuzzleLogic>[]) PuzzleLogic.class.getPermittedSubclasses())).map(NewPuzzleType::new).toList();
    public static final Map<String,NewPuzzleType> NAME_TO_VALUE = VALUES.stream().collect(Collectors.toMap(NewPuzzleType::getSerializedName, Function.identity()));

    @Override
    @NotNull
    public String getSerializedName() {
        var endstr = "Logic";
        var className = type.getSimpleName();
        if(className.endsWith(endstr))className = className.substring(0,className.length()-endstr.length());
        className = className.chars().mapToObj((int c)->{
            if(Character.isUpperCase(c)){
                return "_"+(char)Character.toLowerCase(c);
            }
            return ""+(char)c;
        }).reduce(String::concat).get();
        if(className.startsWith("_"))return className.substring(1);
        return className;
    }

    @Override
    public int compareTo(@NotNull NewPuzzleType o) {
        return Integer.compare(hashCode(),hashCode());
    }

    @Override
    public int hashCode() {
        return getSerializedName().hashCode();
    }
}
