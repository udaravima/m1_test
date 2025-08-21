package com.sdp.m1.Extractors;

import java.io.FileWriter;
import java.lang.reflect.Array;
import java.time.Duration;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.experimental.FieldNameConstants;
// import com.sdp.m1.Runner.TestConfigs;

/* 
// TODO:
//
// - Good Naming scheme
// - If the type is 'hidden' of a element ignore :)
// - add checks for css selector and 'hidden' (bool vals) :)
// - Add Test name to the file 
// - Adjust the text not just element.text() but with element.attr("placeholder") if exists or with other info
// - Make this as modulable 
// - Login and session feature fix
// - Select tag upgrades! :)
*/
public class WebPageExtractorJSON {

    private static final Boolean SELECT_HIDDEN = false;
    private static final Boolean USE_CSS_SELECTOR = false;
    private static final Boolean USE_COOKIE = false;
    // private static final String USE_URL =
    // "https://m1-impl.hsenidmobile.com/cas/login";
    // private static final String USE_URL = "https://google.com";
    // private static final String USE_URL = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
    private static final String USE_URL = "https://facebook.com";

    private static final String COOKIE_NAME = "JSESSIONID";
    private static final Cookie COOKIE = new Cookie(COOKIE_NAME, "37B78748D50060E1BDAD9A82BDBA6005");

    private static class Component {
        String type; // e.g. form, navbar, section
        String tag; // HTML tag name
        String text; // visible text
        String id;
        String classes;
        String ariaLabel;
        String selector; // CSS selector
        String role;
        Map<String, Integer> boundingBox = new HashMap<>();
        Set<Map<String, String>> actions = new HashSet<>();
        Set<Map<String, String>> fields = new HashSet<>();
        Map<String, String> attributes = new HashMap<>();
    }

    public static void main(String[] args) throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        // new WebDriverWait(driver, Duration.ofSeconds(10))
        // .until(ExpectedConditions.presenceOfElementLocated(By.tagName("form")));
        driver.get(USE_URL);

        if (USE_COOKIE) {
            driver.manage().deleteCookieNamed(COOKIE_NAME);
            driver.manage().addCookie(COOKIE);
            Thread.sleep(1000); // Wait for cookie to be set
            driver.navigate()
                    .to(USE_URL);

            System.out.println("Navigated with cookies");
        }

        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = String.format("target/ExJson/page_components_%s_%s.json", timestamp,
                Math.abs(USE_URL.hashCode()));

        System.out.println("Page source loaded, waiting for elements...");
        Thread.sleep(5000); // let the page load first
        System.out.println("Done waiting!");
        String pageSource = driver.getPageSource();
        Document doc = Jsoup.parse(pageSource);

        List<Component> components = new ArrayList<>();

        // Main semantic elements
        extractByTag(doc, driver, "form", "form", components);
        extractByTag(doc, driver, "nav", "navbar", components);
        extractByTag(doc, driver, "header", "header", components);
        extractByTag(doc, driver, "aside", "sidebar", components);
        extractByTag(doc, driver, "main", "main", components);
        extractByTag(doc, driver, "footer", "footer", components);

        // // Fallback: sections and large DIVs
        extractByTag(doc, driver, "div", "section", components);

        // Save JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        // create directories if not exists
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("target/ExJson"));
        } catch (java.io.IOException e) {
            System.err.println("Failed to create directories: " + e.getMessage());
        }

        try (FileWriter fw = new FileWriter(fileName)) {
            gson.toJson(components, fw);
        } catch (Exception e) {
            System.err.println("Failed to write JSON file: " + e.getMessage());
        }

        System.out.printf("Extracted %d components -> %s%n", components.size(), fileName);
        driver.quit();
    }

    private static void extractByTag(Document doc, WebDriver driver, String tag, String type,
            List<Component> components) {

        Elements elements = doc.select(tag);

        for (Element el : elements) {
            try {
                // Locate in Selenium DOM
                WebElement we;
                if (el.id().length() > 0) {
                    we = driver.findElement(By.id(el.id()));
                } else {
                    we = driver.findElement(By.xpath(getXPath(el)));
                }

                Component c = new Component();
                c.type = type;
                c.tag = el.tagName();
                c.text = el.ownText().trim();
                c.id = el.id();
                c.classes = el.className();
                c.role = el.attr("role");
                c.selector = buildSelector(el);
                if (el.attr("aria-label").length() > 0)
                    c.ariaLabel = el.attr("aria-label");

                // Collect all attributes
                el.attributes().forEach(attr -> c.attributes.put(attr.getKey(), attr.getValue()));

                // Bounding box
                Map<String, Integer> box = new HashMap<>();
                Point p = we.getLocation();
                Dimension d = we.getSize();
                box.put("x", p.getX());
                box.put("y", p.getY());
                box.put("width", d.getWidth());
                box.put("height", d.getHeight());
                if (we.isDisplayed())
                    c.boundingBox = box;

                Elements buttons = el.select("button, input[type=submit], input[type=button], input[type=reset], a");
                Elements inputs = el.select("input, textarea, select");
                fieldBuilder(buttons, c.actions, doc, null);
                fieldBuilder(inputs, c.fields, doc, new ArrayList<>(Arrays.asList("submit", "reset", "button")));

                // Extract actions (buttons, submit, links)
                // Add actions from the current element
                // for (Element b : buttons) {
                // if (b.attr("type").equals("hidden") && !SELECT_HIDDEN)
                // continue; // Skip hidden elements unless explicitly selected
                // Map<String, String> actionMeta = new HashMap<>();
                // actionMeta.put("selector", buildSelector(b));
                // actionMeta.put("text", b.text().trim());
                // actionMeta.put("type", b.tagName());
                // actionMeta.put("name", b.attr("name"));
                // actionMeta.put("role", b.attr("role"));
                // actionMeta.put("value", b.attr("value"));
                // if (b.attr("aria-label").length() > 0)
                // actionMeta.put("aria-label", b.attr("aria-label"));
                // if (b.attr("href").length() > 0)
                // actionMeta.put("href", b.attr("href"));
                // c.actions.add(actionMeta);
                // }

                // Extract fields (inputs, selects, textareas)
                // Add fields from the current element
                // for (Element f : inputs) {
                // if (f.attr("type").equals("hidden") && !SELECT_HIDDEN)
                // continue; // Skip hidden elements unless explicitly selected
                // if ("submit".equals(f.attr("type")) || "button".equals(f.attr("type"))
                // || "reset".equals(f.attr("type")))
                // continue;

                // Map<String, String> fieldMeta = new HashMap<>();
                // fieldMeta.put("selector", buildSelector(f));
                // fieldMeta.put("text", f.text().trim());
                // fieldMeta.put("type", f.attr("type").isEmpty() ? f.tagName() :
                // f.attr("type"));
                // fieldMeta.put("name", f.attr("name"));
                // fieldMeta.put("role", f.attr("role"));
                // fieldMeta.put("value", f.attr("value"));
                // fieldMeta.put("placeholder", f.attr("placeholder"));
                // fieldMeta.put("label", findLabelText(doc, f));
                // if (f.attr("aria-label").length() > 0)
                // fieldMeta.put("aria-label", f.attr("aria-label"));

                // c.fields.add(fieldMeta);
                // }

                // // Avoid duplicates (same selector + type)
                // boolean duplicate = components.stream()
                // .anyMatch(existing -> existing.selector.equals(c.selector) &&
                // existing.type.equals(c.type) ||
                // (existing.fields.equals(c.fields) && existing.actions.equals(c.actions)));

                // if (!duplicate) {
                // components.add(c);
                // }

                // Avoid duplicates by selector+type
                boolean duplicateSelector = components.stream()
                        .anyMatch(existing -> existing.selector.equals(c.selector) && existing.type.equals(c.type));
                if (duplicateSelector) {
                    continue;
                }

                // Avoid nested duplicates (subset of another component’s fields+actions)
                boolean subset = components.stream().anyMatch(
                        existing -> existing.fields.containsAll(c.fields) && existing.actions.containsAll(c.actions));
                if (subset) {
                    continue;
                }

                // Only add if it's a "new" component
                components.add(c);

            } catch (Exception ignored) {
                System.out.printf("Failed to extract component: %s%n", ignored.getMessage());
            }
        }
    }

    private static Set<Map<String, String>> fieldBuilder(Elements elements, Set<Map<String, String>> fieldSets,
            Document doc, ArrayList<String> ignoreTypes) {
        if (elements == null || elements.isEmpty()) {
            return fieldSets;
        }

        for (Element element : elements) {
            if (element.attr("type").equals("hidden") && !SELECT_HIDDEN) {
                continue; // Skip hidden elements unless explicitly selected
            }
            String type = element.attr("type");
            if (ignoreTypes != null && ignoreTypes.contains(type)) {
                continue;
            }

            Map<String, String> meta = new HashMap<>();
            meta.put("selector", buildSelector(element));
            meta.put("text", element.text().trim());
            meta.put("type", element.attr("type").isEmpty() ? element.tagName() : element.attr("type"));
            meta.put("name", element.attr("name"));
            meta.put("role", element.attr("role"));
            meta.put("value", element.attr("value"));
            meta.put("placeholder", element.attr("placeholder"));
            String label = findLabelText(doc, element);
            if (label != null && !label.isEmpty()) {
                meta.put("label", label);
            }

            if (element.tagName().equals("select")) {
                // Handle <select> elements
                meta.put("options", String.join(",", getSelectOptions(element)));
            }
            if (element.attr("href").length() > 0)
                meta.put("href", element.attr("href"));
            if (element.attr("aria-label").length() > 0)
                meta.put("aria-label", element.attr("aria-label"));

            fieldSets.add(meta);
        }
        return fieldSets;
    }

    private static Set<String> getSelectOptions(Element selectElement) {
        Set<String> options = new HashSet<>();
        for (Element option : selectElement.children()) {
            if (option.tagName().equals("option")) {
                options.add(option.text().trim());
            }
        }
        return options;
    }

    private static String buildSelector(Element el) {
        if (!el.id().isEmpty()) {
            // Chrome prefers ID because it's unique
            return "#" + el.id();
        } else if (!el.className().isEmpty() && USE_CSS_SELECTOR) {
            // multiple classes -> join with dots
            return el.tagName() + "." + el.className().trim().replace(" ", ".");
        } else {
            return getXPath(el);
        }
    }

    // private static String getXPath(Element el) {
    // List<String> path = new ArrayList<>();
    // Element current = el;
    // while (current != null && !current.tagName().equals("html")) {
    // int index = 1;
    // Element sibling = current.previousElementSibling();
    // while (sibling != null) {
    // if (sibling.tagName().equals(current.tagName()))
    // index++;
    // sibling = sibling.previousElementSibling();
    // }
    // path.add(0, "/" + current.tagName() + "[" + index + "]");
    // current = current.parent();
    // }
    // return "/html" + String.join("", path);
    // }

    private static String getXPath(Element el) {
        if (el == null)
            return null;

        // If element has an ID, DevTools returns that directly
        if (!el.id().isEmpty()) {
            return "//*[@id='" + el.id() + "']";
        }

        List<String> path = new ArrayList<>();
        Element current = el;

        while (current != null && !current.tagName().equals("html")) {
            // If we got a id then we can get it from there.
            if (!current.id().isEmpty()) {
                path.add(0, "//*[@id='" + current.id() + "']");
                // break; // ID is unique, no need to go further
                return String.join("/", path); // Return early if ID found
            }

            int index = 1;
            Element sibling = current.previousElementSibling();
            while (sibling != null) {
                if (sibling.tagName().equals(current.tagName())) {
                    index++;
                }
                sibling = sibling.previousElementSibling();
            }
            path.add(0, current.tagName() + "[" + index + "]");

            current = current.parent();
        }

        return "/html/" + String.join("/", path);
    }

    private static String getCssSelector(Element el, Document doc) {
        if (el == null)
            return null;

        // If element has an ID, Chrome uses CSS ID
        if (!el.id().isEmpty()) {
            return "#" + el.id();
        }

        List<String> parts = new ArrayList<>();
        Element current = el;

        while (current != null && !current.tagName().equals("html")) {
            StringBuilder part = new StringBuilder(current.tagName());

            // Add classes (first class only, DevTools often picks shortest unique form)
            if (!current.className().isEmpty()) {
                String[] classes = current.className().split("\\s+");
                for (String cls : classes) {
                    part.append(".").append(cls);
                }
            }

            // Add :nth-of-type() if needed
            int index = 1;
            Element sibling = current.previousElementSibling();
            while (sibling != null) {
                if (sibling.tagName().equals(current.tagName())) {
                    index++;
                }
                sibling = sibling.previousElementSibling();
            }
            if (index > 1) {
                part.append(":nth-of-type(").append(index).append(")");
            }

            parts.add(0, part.toString());

            String candidate = String.join(" > ", parts);

            // --- uniqueness check like Chrome does ---
            if (doc.select(candidate).size() == 1) {
                return candidate;
            }

            current = current.parent();
        }

        return String.join(" > ", parts);
    }

    // Find <label for="id"> text if available
    private static String findLabelText(Document doc, Element input) {
        String id = input.id();
        if (id != null && !id.isEmpty()) {
            Element label = doc.selectFirst("label[for=" + id + "]");
            if (label != null) {
                return label.text();
            }
        }
        String name = input.attr("name");
        if (name != null && !name.isEmpty()) {
            Element label = doc.selectFirst("label[for=" + name + "]");
            if (label != null) {
                return label.text();
            }
        }
        return "";
    }
}