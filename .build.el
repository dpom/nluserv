;; project settings
(setq ent-project-home (file-name-directory (if load-file-name load-file-name buffer-file-name)))
(setq ent-project-name "nluserv") 
(setq ent-clean-regexp "~$\\|\\.tex$")
(setq ent-project-config-filename "README.org")

;; local functions

(defvar project-version)

(setq project-version (ent-get-version))

(setq ent-cli '(("templates/nlp.conf.cli" . "target/nlp.conf")
                ("templates/run_nlp.sh.cli" . "target/run_nlp.sh")
                ))

(defun make-image-tag (&optional version)
   "Make docker image tag using ent variables."
   (concat "dpom/" ent-project-name ":" (or version project-version)))

;; tasks

(load ent-init-file)

;; (task 'org2md '() "convert org doc to md" '(lambda (&optional x) "cd docs; make all; cd .."))

;; (task 'api '() "build the API documentation" '(lambda (&optional x) "lein codox"))

;; (task 'doc '(org2md api) "build the project documentation" '(lambda (&optional x) "ls docs"))

(task 'format '() "format the project" '(lambda (&optional x) "lein cljfmt fix"))

(task 'check '() "check the project" '(lambda (&optional x) "lein do check, kibit, eastwood"))

(task 'tree '() "tree dependencies" '(lambda (&optional x) "lein do clean, deps :tree"))

(task 'tests '() "run tests" '(lambda (&optional x) "lein do clean, test"))

(task 'libupdate () "update project libraries" '(lambda (&optional x) "lein ancient :no-colors"))

(task 'uberjar '() "make the uberjar" '(lambda (&optional x) "lein do clean, uberjar"))

(task 'gencli '() "generate script files" '(lambda (&optional x) (ent-emacs "ent-make-all-cli-files"
                                                                            (expand-file-name ent-file-name ent-project-home))))

(task 'stage '(uberjar gencli) "deploy to stage" '(lambda (&optional x) "nlpsync"))

(task 'run '() "run the server" '(lambda (&optional x) "lein run"))

;; (task 'deploy '() "deploy to clojars" '(lambda (&optional x) "lein deploy clojars"))

(task 'deps '() "load libs" '(lambda (&optional x) "lein deps"))

;; (task 'heroku '(deps) "deploy to heroku" '(lambda (&optional x) "heroku container:push web"))

;; (task 'build '(deps) "build docker image" '(lambda (&optional x) (concat "docker"
;;                                                                                " build"
;;                                                                                " -t " (make-image-tag)
;;                                                                                " -t " (make-image-tag "latest")
;;                                                                                " .")))

;; (task 'dockerpush '(build) "push image to docker hub" '(lambda (&optional x) (concat "docker push " (make-image-tag)
;;                                                                                            ";docker push " (make-image-tag "latest"))))


;; Local Variables:
;; no-byte-compile: t
;; no-update-autoloads: t
;; End:
;; project settings
