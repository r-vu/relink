"use strict";

const rest = require("rest");
const defaultRequest = require("rest/interceptor/defaultRequest");
const mime = require("rest/interceptor/mime");
const uriTemplateInterceptor = require("./api/uriTemplateInterceptor");
const errorCode = require("rest/interceptor/errorCode");
const baseRegistry = require("rest/mime/registry");
const template = require("rest/interceptor/template");
const pathPrefix = require("rest/interceptor/pathPrefix")

const registry = baseRegistry.child();

registry.register("text/uri-list", require("./api/uriListConverter"));
registry.register("application/hal+json", require("rest/mime/type/application/hal"));

module.exports = rest
        .wrap(pathPrefix, {prefix: "/api"})
        .wrap(template, {template: "shortURLs{?size}", size: 20})
        .wrap(mime, { registry: registry })
        // .wrap(uriTemplateInterceptor)
        .wrap(errorCode)
        .wrap(defaultRequest, { headers: { "Accept": "application/hal+json" }});
