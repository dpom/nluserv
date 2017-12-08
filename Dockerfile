FROM clojure

ENV HOME_DIR /usr/src/app


# Add the user UID:1001, GID:1001, home at $HOME_DIR
RUN groupadd -r dan -g 1001 && useradd -u 1001 -r -g dan -m -d $HOME_DIR -s /sbin/nologin -c "Dan" dan && \
    chmod 755 $HOME_DIR

RUN adduser dan root

COPY project.clj $HOME_DIR

# RUN lein deps

COPY . $HOME_DIR

RUN chgrp -R 0 $HOME_DIR && chmod -R g=u $HOME_DIR

WORKDIR $HOME_DIR

USER 1001

CMD lein run 
