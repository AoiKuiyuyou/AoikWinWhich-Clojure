;;
(ns aoikwinwhich)

(require '[clojure.string :refer [join]])
(require '[clojure.string :refer [split]])
(import java.io.File)
(import java.lang.System)
(import java.nio.file.Files)
(import java.nio.file.LinkOption)
(import java.nio.file.Paths)
(import java.util.LinkedList)

;;
(defn -path_make
[part_s]
    (.toString (Paths/get "" (into-array part_s)))
)

(defn -file_exists
[path]
    (Files/isRegularFile (Paths/get "" (into-array [path])) (make-array LinkOption 0))
)

(defn -find_executable
[prog]
    (let [
        ;; 8f1kRCu
        env_var_PATHEXT (. System getenv "PATHEXT")
        ;;; can be nil

        ;; 4ysaQVN
        env_var_PATH (. System getenv "PATH")
        ;;; can be nil

        val_sep_re (re-pattern File/pathSeparator)
        ]

        ;;
        (let [
            ext_s
                ;; 2fT8aRB
                ;; uniquify
                (distinct
                    ;; 2zdGM8W
                    ;; convert to lowercase
                    (map #(.toLowerCase %1)
                        ;; 2gqeHHl
                        ;; remove empty
                        (filter #(not (= %1 ""))
                            ;; 2pGJrMW
                            ;; strip
                            (map #(.trim %1)
                                ;; 6qhHTHF
                                ;; split into a list of extensions
                                (if (nil? env_var_PATHEXT)
                                    ([])
                                    (split env_var_PATHEXT val_sep_re)
                                )
                            )
                        )
                    )
                )
            ]

            ;;
            (let [
                dir_path_s
                    ;; 2klTv20
                    ;; uniquify
                    (distinct
                        ;; 5rT49zI
                        ;; insert empty dir path to the beginning
                        ;;
                        ;; Empty dir handles the case that |prog| is a path, either relative or
                        ;;  absolute. See code 7rO7NIN.
                        (into [""]
                            ;; 6mPI0lg
                            (if (nil? env_var_PATH)
                                ([])
                                (split env_var_PATH val_sep_re)
                            )
                        )
                    )
                ]

                ;; 6bFwhbv
                (let [
                    exe_path_s (LinkedList.)
                    prog_lower (.toLowerCase prog)
                    prog_has_ext
                        (some #(. prog_lower endsWith %1) ext_s)
                    ]
                    (doseq [dir_path dir_path_s]
                        ;; 7rO7NIN
                        ;; synthesize a path with the dir and prog
                        (let [
                            path
                                (if (= dir_path "")
                                    prog
                                    (-path_make [dir_path prog])
                                )

                            ]

                            ;; 6kZa5cq
                            ;; assume the path has extension, check if it is an executable
                            (if (and prog_has_ext (-file_exists path))
                                (. exe_path_s add path)
                                ()
                            )

                            ;; 2sJhhEV
                            ;; assume the path has no extension
                            (doseq [ext ext_s]
                                ;; 6k9X6GP
                                ;; synthesize a new path with the path and the executable extension
                                (let [
                                    path_plus_ext (str path ext)
                                    ]

                                    ;; 6kabzQg
                                    ;; check if it is an executable
                                    (if (-file_exists path_plus_ext)
                                        (. exe_path_s add path_plus_ext)
                                        ()
                                    )
                                )
                            )
                        )
                    )

                    ;; func res
                    exe_path_s
                )
            )
        )
    )
)

(defn -main
[]
;; 9mlJlKg
;; check if one cmd arg is given
    (if (not (= 1 (count *command-line-args*)))
        (do
            ;; 7rOUXFo
            ;; print program usage
            (println "Usage: aoikwinwhich PROG")
            (println "")
            (println "#/ PROG can be either name or path")
            (println "aoikwinwhich notepad.exe")
            (println "aoikwinwhich C:\\Windows\\notepad.exe")
            (println "")
            (println "#/ PROG can be either absolute or relative")
            (println "aoikwinwhich C:\\Windows\\notepad.exe")
            (println "aoikwinwhich Windows\\notepad.exe")
            (println "")
            (println "#/ PROG can be either with or without extension")
            (println "aoikwinwhich notepad.exe")
            (println "aoikwinwhich notepad")
            (println "aoikwinwhich C:\\Windows\\notepad.exe")
            (println "aoikwinwhich C:\\Windows\\notepad")

            ;; 3nqHnP7
            ()
        )
        (do
            ;; 9m5B08H
            ;; get name or path of a program from cmd arg
            (let [prog (nth *command-line-args* 0)]
                ;; 8ulvPXM
                ;; find executables
                (let [path_s (-find_executable prog)]
                    (if (= (.size path_s) 0)
                        ;; 5fWrcaF
                        ;; has found none, exit
                        ;; 3uswpx0
                        ()

                        ;; 9xPCWuS
                        ;; has found some, output
                        (do
                            (println (join "\n" path_s))

                            ;; 4s1yY1b
                            ()
                        )
                    )
                )
            )
        )
    )
)

(-main)
