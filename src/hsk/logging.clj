(ns hsk.logging)
(import '(org.apache.log4j Logger WriterAppender SimpleLayout))
;; adapted from src/clj/cascalog/playground in Cascalog by Nathan Marz.
(defmacro enable-logging []
  '(do
     (import (quote [java.io PrintStream]))
     (import (quote [cascalog WriterOutputStream])) 
     (import (quote [org.apache.log4j Logger WriterAppender SimpleLayout]))
     (.addAppender (Logger/getRootLogger) (WriterAppender. (SimpleLayout.) *out*))
     (System/setOut (PrintStream. (WriterOutputStream. *out*)))))
