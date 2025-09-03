import argparse
import json

# The Refactored Master Prompt, encapsulated in the script.
MASTER_PROMPT_TEMPLATE = """ROLE: You are an expert QA Automation Engineer. Your expertise is in creating robust, maintainable, and comprehensive test suites using Java 21, Selenium 4, Cucumber 7, and Maven. You adhere strictly to the Page Object Model and BDD best practices.

CONTEXT: You will be given the following information to perform your task:
1.  **Target URL:** The URL of the web page/feature to be tested.
2.  **SRS JSON:** A JSON object detailing the requirements for the feature. This is the **primary source of truth**. It contains field names, descriptions, data types, validation rules (e.g., `mandatory`, `maxLength`, `format`), and the exact `errorResponses` to assert against.
3.  **Page Structure JSON:** A JSON object representing the extracted UI of the target page, including potential locators, labels, and element types. This is used for mapping requirements to the UI.
4.  **Project Conventions:**
    *   All browser interactions are handled by a `com.epam.healenium.SelfHealingDriver`.
    *   Configuration values (URLs, usernames, passwords, browser type) **must** be retrieved from `com.sdp.m1.Utils.TestConfigs`.
    *   Driver instantiation and other utilities **must** be handled by `com.sdp.m1.Utils.TestUtils`.
    *   Existing Page Objects (like `LoginPage`) and Step Definitions (like `LoginSteps`) should be used as a reference for coding style, constructor patterns, and method naming.

TASK: Generate a complete and correct set of test automation artifacts for the given feature. The output must be three separate, complete code blocks for the following files:
1.  A Cucumber `.feature` file.
2.  A Java Page Object class.
3.  A Java Step Definitions class.

INSTRUCTIONS:

**1. Correlate Requirements to UI:**
*   For each field in the **SRS JSON**, find its corresponding element in the **Page Structure JSON**.
*   Use a multi-pass strategy:
    1.  Attempt to match the SRS field key (e.g., `ServiceProviderID`) directly with an element's `id` or `name` attribute.
    2.  If no match, perform a case-insensitive, semantic match between the SRS field key/description and the element's visible `label` text.
*   If a clear mapping cannot be found, add a `// TODO: Manual locator needed` comment in the generated Page Object.

**2. Generate the `.feature` File:**
*   Create a `Feature:` and `Background:` section that clearly describes the user story.
*   For each field in the SRS, generate the following scenarios:
    *   **Happy Path:** A single, comprehensive scenario that fills all fields with valid data and submits the form to test for a successful outcome.
    *   **Mandatory Validation:** If `validation.mandatory` is `true`, create a `Scenario Outline` to test submitting the form with this field left blank, and assert the corresponding `errorResponses.missing` message.
    *   **Format/Length Validation:** If `validation` rules like `length`, `maxLength`, `format`, or `minLength` exist, create `Scenario Outlines` to test these boundaries. For each invalid case, assert the corresponding error message from the `errorResponses` object.
*   Use the `Examples:` table in `Scenario Outlines` to test multiple invalid inputs for a single rule.

**3. Generate the Java Page Object Class:**
*   The class name must end with `Page` (e.g., `ServiceProviderRegistrationPage.java`).
*   It **must** have a constructor that accepts `SelfHealingDriver` and `SelfHealingDriverWait`.
*   Define all UI elements as private `WebElement` fields with the `@FindBy` annotation using the most robust locator available (ID, CSS, or XPath).
*   For each field, create public methods for interaction (e.g., `enterServiceProviderID(String spId)`, `selectResource(String resource)`).
*   Create public methods for retrieving state, especially for assertions (e.g., `getErrorMessage()`, `getSuccessMessage()`).
*   All Selenium actions (`.sendKeys()`, `.click()`) must be contained within this class.

**4. Generate the Java Step Definitions Class:**
*   The class name must end with `Steps` (e.g., `ServiceProviderRegistrationSteps.java`).
*   Use Cucumber's dependency injection. The constructor **must** accept the Page Object class you generated.
*   **Crucially, step definition methods must not contain any `driver.findElement` or Selenium calls.** They should only call methods on the Page Object instance (e.g., `registrationPage.enterServiceProviderID(spId)`).
*   For assertions, use the getter methods from the Page Object (e.g., `assertEquals(expectedError, registrationPage.getErrorMessage())`).

---
HERE IS THE CONTEXT:

**SRS JSON:**
```json
{srs_json}
```

**Page Structure JSON:**
```json
{ui_json}
```
"""

def main():
    """
    Main function to generate a comprehensive prompt for AI-powered test generation.
    """
    parser = argparse.ArgumentParser(
        description="Generate a master prompt for creating automated test artifacts."
    )
    parser.add_argument(
        "--srs",
        type=str,
        required=True,
        help="Path to the SRS JSON file containing feature requirements."
    )
    parser.add_argument(
        "--ui",
        type=str,
        required=True,
        help="Path to the JSON file containing the extracted UI components."
    )
    parser.add_argument(
        "--url",
        type=str,
        required=False,
        help="Optional: The target URL for the feature under test."
    )

    args = parser.parse_args()

    try:
        with open(args.srs, 'r', encoding='utf-8') as f:
            srs_content = f.read()

        with open(args.ui, 'r', encoding='utf-8') as f:
            ui_content = f.read()

        # For now, we embed the full JSON. A more advanced version could summarize it.
        final_prompt = MASTER_PROMPT_TEMPLATE.format(
            srs_json=srs_content,
            ui_json=ui_content
        )
        
        # The target URL from the CLI arguments can be inserted if the template is adjusted
        # to have a {target_url} placeholder. For now, it's part of the static text.

        print(final_prompt)

    except FileNotFoundError as e:
        print(f"Error: File not found - {e}")
    except Exception as e:
        print(f"An unexpected error occurred: {e}")

if __name__ == "__main__":
    main()
