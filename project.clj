(defproject vilma "0.1.0-SNAPSHOT"
  :description "Vilma is a library of commonly used financial and statistical functions"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [orchestra "2019.02.06-1"]]

  :repl-options {:init-ns vilma.core}
  :profiles {:dev {:dependencies [[midje "1.9.9"]]}})
