name := "ipe-toolkit-sample"


unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar")) // fix for `sbt run`