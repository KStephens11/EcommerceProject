function fn() {

    var port = karate.properties['local.server.port'];

    return {
        baseUrl: 'http://localhost:' + port
    };
}