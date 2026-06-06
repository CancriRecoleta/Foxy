//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model.obj;

import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import joptsimple.internal.Strings;
import org.jetbrains.annotations.Nullable;

public class ObjTokenizer implements AutoCloseable {
    private final BufferedReader lineReader;

    public ObjTokenizer(InputStream inputStream) {
        this.lineReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
    }

    public @Nullable String[] readAndSplitLine(boolean ignoreEmptyLines) throws IOException {
        do {
            String currentLine = this.lineReader.readLine();
            if (currentLine == null) {
                return null;
            }

            List<String> lineParts = new ArrayList();
            if (currentLine.startsWith("#")) {
                currentLine = "";
            }

            boolean hasContinuation;
            if (currentLine.length() > 0) {
                do {
                    hasContinuation = currentLine.endsWith("\\");
                    String tmp = hasContinuation ? currentLine.substring(0, currentLine.length() - 1) : currentLine;
                    Stream var10000 = Arrays.stream(tmp.split("[\t ]+")).filter((s) -> {
                        return !Strings.isNullOrEmpty(s);
                    });
                    Objects.requireNonNull(lineParts);
                    var10000.forEach(lineParts::add);
                    if (hasContinuation) {
                        currentLine = this.lineReader.readLine();
                        if (currentLine == null || currentLine.length() == 0 || currentLine.startsWith("#")) {
                            break;
                        }
                    }
                } while(hasContinuation);
            }

            if (lineParts.size() > 0) {
                return (String[])lineParts.toArray(new String[0]);
            }
        } while(ignoreEmptyLines);

        return new String[0];
    }

    public void close() throws IOException {
        this.lineReader.close();
    }
}
