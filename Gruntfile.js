'use strict';

module.exports = function (grunt) {

  grunt.initConfig({

    'http-server': {

        'dev': {

            // the server root directory
            root: "./",

            // the server port
            // can also be written as a function, e.g.
            // port: function() { return 8282; }
            port: 8282,


            // the host ip address
            // If specified to, for example, "127.0.0.1" the server will
            // only be available on that ip.
            // Specify "0.0.0.0" to be available everywhere
            host: "0.0.0.0",

            cache: 60,
            showDir : true,
            autoIndex: true,

            // server default file extension
            ext: "html",

            // run in parallel with other tasks
            runInBackground: false

        }

    }
});

grunt.loadNpmTasks('grunt-http-server');

};
