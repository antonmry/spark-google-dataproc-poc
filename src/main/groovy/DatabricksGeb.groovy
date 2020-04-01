import geb.Browser
import org.codehaus.groovy.runtime.InvokerHelper

class DatabricksGeb extends Script {

    def run() {
        System.setProperty("webdriver.gecko.driver", "geckodriver");

        def organization = login(PrivateData.email, PrivateData.password)
        String clusterName = UUID.randomUUID().toString()
        clusterName = "1a0e92e7-2b4a-4745-be31-5a4e5b7289a7"
        //createCluster(clusterName)
        installLibrary("com.github.potix2:spark-google-spreadsheets_2.11:0.6.3", organization, clusterName)
        installLibrary("ch.cern.sparkmeasure:spark-measure_2.12:0.16", organization, clusterName)
        //runNotebook(organization, "469897532896452") // covid-coronadatascraper
        //runNotebook(organization, "2568995183456180") // covid-kaggle
    }

    String login(String email, String password) {
        return Browser.drive {

            go "https://community.cloud.databricks.com/login.html"
            waitFor(20) { title == "Login - Databricks Community Edition" }

            $(name: "j_username").value(email)
            assert $(name: "j_username").value() == email

            $(name: "j_password").value(password)
            assert $(name: "j_password").value() == password

            $("button", text: "Sign In").click()

            waitFor(20) { title == "Databricks Community Edition" }
        }.currentUrl.replace("https://community.cloud.databricks.com/?o=", "")

    }

    def createCluster(String clusterName) {

        Browser.drive {
            go "https://community.cloud.databricks.com/"
            waitFor(10) { title == "Databricks Community Edition" }

            $("a", text: "New Cluster").click()

            waitFor(10) { title == "Create Cluster - Databricks Community Edition" }


            waitFor { $("form").find("input", id: "cluster-input--name") }
            $("form").find("input", id: "cluster-input--name").value(clusterName)
            assert $("form").find("input", id: "cluster-input--name").value() == clusterName

            waitFor { $("input", type: "submit") }
            $("input", type: "submit").click()


            waitFor(10) { title == "Clusters - Databricks Community Edition" }
            waitFor(300) {
                $("div", text: "Running")
            }
        }
    }

    def installLibrary(String library, String organization) {

        Browser.drive {
            go "https://community.cloud.databricks.com/?o=" + organization + "#setting/clusters"
            waitFor(10) { title == "Clusters - Databricks Community Edition" }

            driver.navigate().refresh()

            waitFor(300) {
                $("div", text: "Running")
            }
            $("div", text: "Running").click()

            waitFor { $("a", text: "Libraries") }
            $("a", text: "Libraries").click()

            int librariesCount = 0

            if ($("span", text: "Installed").displayed)
                librariesCount = $("span", text: "Installed").size()

            waitFor { $("a", text: "Install New") }
            $("a", text: "Install New").click()

            waitFor { $("button", text: "Maven") }
            $("button", text: "Maven").click()

            waitFor { $("input", name: "MAVEN.coordinates") }
            $("input", name: "MAVEN.coordinates").value(library)
            $("input", name: "MAVEN.repo").value("https://repo.maven.apache.org/maven2")
            $("span", text: "Install").click()

            waitFor(30) {
                $("span", text: "Installed").displayed &&
                        $("span", text: "Installed").size() == librariesCount + 1
            }

        }
    }

    def runNotebook(String organization, String notebookId) {

        Browser.drive {
            go "https://community.cloud.databricks.com/?o=" + organization + "#notebook/" + notebookId

            waitFor(20) { $("a", id: "clear-results-link") }
            $("a", id: "clear-results-link").click()

            waitFor() { $("a", text: "Clear Results") }
            $("a", text: "Clear Results").click()

            waitFor() { $("a", text: "Confirm") }
            $("a", text: "Confirm").click()

            waitFor(10) { $("a[data-name='Run All']") }
            $("a[data-name='Run All']").click()

            try {
                waitFor() { $("a", text: "Attach and Run") }
                $("a", text: "Attach and Run").click()
            } catch (geb.waiting.WaitTimeoutException e) {
                // cluster currently attached
            }

            waitFor(Double.MAX_VALUE) { $("a[data-name='Run All']") }
        }
    }


    static void main(String[] args) {
        InvokerHelper.runScript(DatabricksGeb, args)
    }

}
