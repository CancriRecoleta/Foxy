//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JUnitLikeTestReporter implements TestReporter {
    private final Document document;
    private final Element testSuite;
    private final Stopwatch stopwatch;
    private final File destination;

    public JUnitLikeTestReporter(File p_177664_) throws ParserConfigurationException {
        this.destination = p_177664_;
        this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        this.testSuite = this.document.createElement("testsuite");
        Element $$1 = this.document.createElement("testsuite");
        $$1.appendChild(this.testSuite);
        this.document.appendChild($$1);
        this.testSuite.setAttribute("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        this.stopwatch = Stopwatch.createStarted();
    }

    private Element createTestCase(GameTestInfo p_177671_, String p_177672_) {
        Element $$2 = this.document.createElement("testcase");
        $$2.setAttribute("name", p_177672_);
        $$2.setAttribute("classname", p_177671_.getStructureName());
        $$2.setAttribute("time", String.valueOf((double)p_177671_.getRunTime() / 1000.0));
        this.testSuite.appendChild($$2);
        return $$2;
    }

    public void onTestFailed(GameTestInfo p_177669_) {
        String $$1 = p_177669_.getTestName();
        String $$2 = p_177669_.getError().getMessage();
        Element $$4;
        if (p_177669_.isRequired()) {
            $$4 = this.document.createElement("failure");
            $$4.setAttribute("message", $$2);
        } else {
            $$4 = this.document.createElement("skipped");
            $$4.setAttribute("message", $$2);
        }

        Element $$5 = this.createTestCase(p_177669_, $$1);
        $$5.appendChild($$4);
    }

    public void onTestSuccess(GameTestInfo p_177674_) {
        String $$1 = p_177674_.getTestName();
        this.createTestCase(p_177674_, $$1);
    }

    public void finish() {
        this.stopwatch.stop();
        this.testSuite.setAttribute("time", String.valueOf((double)this.stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0));

        try {
            this.save(this.destination);
        } catch (TransformerException var2) {
            TransformerException $$0 = var2;
            throw new Error("Couldn't save test report", $$0);
        }
    }

    public void save(File p_177667_) throws TransformerException {
        TransformerFactory $$1 = TransformerFactory.newInstance();
        Transformer $$2 = $$1.newTransformer();
        DOMSource $$3 = new DOMSource(this.document);
        StreamResult $$4 = new StreamResult(p_177667_);
        $$2.transform($$3, $$4);
    }
}
