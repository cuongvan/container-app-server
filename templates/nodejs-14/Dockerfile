FROM node:14.4.0

RUN useradd --create-home ckanapp

RUN mkdir /inputs /outputs /app && chown -R ckanapp:ckanapp /inputs /outputs /app
USER ckanapp
WORKDIR /app
COPY --chown=ckanapp:ckanapp package.json .
RUN npm install
COPY --chown=ckanapp:ckanapp . .
CMD ["node", "main.js"]
