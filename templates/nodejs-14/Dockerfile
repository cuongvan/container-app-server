FROM node:14.4.0

RUN groupadd -g 20000 ckanapp && useradd --create-home -u 20000 -g ckanapp ckanapp

RUN mkdir /inputs /outputs /app && chown -R ckanapp:ckanapp /inputs /outputs /app

USER ckanapp
WORKDIR /app

COPY --chown=ckanapp:ckanapp package.json .
RUN npm install

COPY --chown=ckanapp:ckanapp . .

ENV TIMEOUT 60

CMD ["sh", "-c", "/usr/bin/timeout --kill-after=5 $TIMEOUT node main.js"]
