package pe.chalk.meal;


import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <chalk@chalk.pe>
 * @since 2017-06-12 14:09
 */
public class Fetcher {
    private static final String MEAL_API = "http://dimigo.in/pages/dimibob_getdata.php?d=%s";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final List<String> KEYS = Arrays.asList("breakfast", "lunch", "dinner", "snack");
    private static final Map<String, Map<String, List<String>>> CACHE = new HashMap<>();

    public static Map<String, List<String>> getMeal(Date date) throws IOException, ScriptException {
        final String api = String.format(MEAL_API, FORMAT.format(date));
        if (CACHE.containsKey(api)) return CACHE.get(api);

        final URL url = new URL(api);
        final StringBuilder buffer = new StringBuilder();

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String read;
            while (Objects.nonNull(read = reader.readLine())) buffer.append(read);
        }

        final String response = buffer.toString();
        if (response.equals("false")) return Collections.emptyMap();

        final ScriptEngineManager manager = new ScriptEngineManager();
        final ScriptEngine engine = manager.getEngineByName("JavaScript");

        final ScriptObjectMirror parseFunction = (ScriptObjectMirror) engine.eval("JSON.parse");
        final ScriptObjectMirror json = (ScriptObjectMirror) parseFunction.call(null, response);

        final Map<String, List<String>> result = KEYS.stream().collect(Collectors.toMap(
                Function.identity(),
                key -> Arrays.asList(json.get(key).toString().split("[/*]"))));

        CACHE.put(api, result);
        return result;
    }
}
