# Specify a base image
FROM node:alpine

#Install some dependencies

WORKDIR /usr/app
COPY ./js/battleship/ /usr/app

RUN npm install --save-dev webpack

COPY . .

# Set up a default command
CMD ["npm","start"]