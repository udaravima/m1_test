import argparse
import json
import os

# The Refactored Master Prompt, which now includes placeholders for code examples.
MASTER_PROMPT_TEMPLATE = """ROLE: You are an expert QA Automation Engineer. Your expertise is in creating robust, maintainable, and comprehensive test suites using Java 21, Selenium 4, Cucumber 7, and Maven. You adhere strictly to the Page Object Model and BDD best practices.

---
CODE STYLE AND STRUCTURE EXAMPLES:
You MUST generate code that strictly follows the style, patterns, and conventions of the examples below.

**1. Feature File Example (`service_provider_registration.feature`):**
Note the use of Background, Scenario Outlines, and Examples tables.
```gherkin
{feature_example}
```

**2. Page Object Class Example (`ServiceProviderRegistrationPage.java`):**
Note the constructor, @FindBy annotations, private WebElements, and public methods for interactions and assertions. All Selenium calls are encapsulated here.
```java
{page_object_example}
```

**3. Step Definition Class Example (`ServiceProviderRegistrationSteps.java`):**
Note the dependency injection in the constructor, how it calls methods on the Page Object, and that it contains NO Selenium `driver` calls.
```java
{steps_example}
```

**4. Configuration Utility (`TestConfigs.java`):**
Use these static methods to get configuration values like URLs and credentials. Do NOT hardcode them.
Available methods include: `getBaseUrl()`, `getAdminUsername()`, `getAdminPassword()`, `getBrowser()`.
```java
{configs_example}
```

**5. General Utility (`TestUtils.java`):**
Use these static methods for common tasks like getting the driver instance (`TestUtils.getDriver()`) or creating explicit waits (`TestUtils.getWaitDriver()`).
```java
{utils_example}
```
---

TASK:
Generate a complete and correct set of test automation artifacts for the given feature. The output must be three separate, complete code blocks for the following files:
1.  A Cucumber `.feature` file.
2.  A Java Page Object class.
3.  A Java Step Definitions class.

INSTRUCTIONS:

**1. Correlate Requirements to UI:**
*   For each field in the **SRS JSON**, find its corresponding element in the **Page Structure JSON**.
*   Use a multi-pass strategy:
    1.  Attempt to match the SRS field key (e.g., `ServiceProviderID`) directly with an element's `id` or `name` or `selector` attribute.
    2.  If no match, perform a case-insensitive, semantic match between the SRS field key/description and the element's visible `label` text.
*   If a clear mapping cannot be found, add a `// TODO: Manual locator needed` comment in the generated Page Object.

**2. Generate the `.feature` File:**
*   Create a `Feature:` and `Background:` section that clearly describes the user story.
*   For each field in the SRS, generate scenarios for the happy path, mandatory validation, and format/length validation based on the `validation` and `errorResponses` objects in the SRS.
*   Use `Scenario Outlines` for validation tests.

**3. Generate the Java Page Object Class:**
*   The class name must end with `Page`.
*   It **must** have a constructor that accepts `SelfHealingDriver` and `SelfHealingDriverWait`.
*   Define all UI elements as private `WebElement` fields with `@FindBy` annotations.
*   Encapsulate all Selenium actions (`.sendKeys()`, `.click()`) in public methods.

**4. Generate the Java Step Definitions Class:**
*   The class name must end with `Steps`.
*   The constructor **must** accept the Page Object class for dependency injection.
*   Step definition methods **must not** contain any `driver.findElement` or Selenium calls. They should only call methods on the Page Object instance.
*   Use JUnit 5 `Assertions.assertEquals` for assertions.

---
HERE IS THE CONTEXT FOR THE NEW FEATURE:

**SRS JSON:**
```json
{srs_json}
```

**Page Structure JSON:**
```json
{ui_json}
```
---
YOUR OUTPUT:
Provide three separate, complete, and immediately usable code blocks for the following files:
1. A new `.feature` file.
2. A new Java Page Object class.
3. A new Java Step Definitions class.
"""

def read_file_content(base_path, file_path):
    """Safely reads content of a file."""
    full_path = os.path.join(base_path, file_path)
    try:
        with open(full_path, 'r', encoding='utf-8') as f:
            return f.read()
    except FileNotFoundError:
        print(f"Warning: Example file not found at {full_path}. Prompt will be less detailed.")
        return f"// Example file not found at: {file_path}"
    except Exception as e:
        print(f"Warning: Error reading {full_path}: {e}")
        return f"// Error reading example file: {file_path}"


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
    
    args = parser.parse_args()

    try:
        # Assuming the script is run from the project root.
        project_root = os.getcwd()
        
        # Read example files
        feature_example = read_file_content(project_root, 'src/test/resources/Features/service_provider_registration.feature')
        page_object_example = read_file_content(project_root, 'src/test/java/com/sdp/m1/Pages/ServiceProviderRegistrationPage.java')
        steps_example = read_file_content(project_root, 'src/test/java/com/sdp/m1/Steps/ServiceProviderRegistrationSteps.java')
        configs_example = read_file_content(project_root, 'src/test/java/com/sdp/m1/Utils/TestConfigs.java')
        utils_example = read_file_content(project_root, 'src/test/java/com/sdp/m1/Utils/TestUtils.java')

        # Read task-specific files
        with open(args.srs, 'r', encoding='utf-8') as f:
            srs_content = f.read()

        with open(args.ui, 'r', encoding='utf-8') as f:
            ui_content = f.read()

        # Format the master prompt with all the context
        final_prompt = MASTER_PROMPT_TEMPLATE.format(
            feature_example=feature_example,
            page_object_example=page_object_example,
            steps_example=steps_example,
            configs_example=configs_example,
            utils_example=utils_example,
            srs_json=srs_content,
            ui_json=ui_content
        )

        print(final_prompt)

    except FileNotFoundError as e:
        print(f"Error: Input file not found - {e}")
    except Exception as e:
        print(f"An unexpected error occurred: {e}")

if __name__ == "__main__":
    main()
