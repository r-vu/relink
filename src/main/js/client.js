"use strict";

const rest = require("rest");
const defaultRequest = require("rest/interceptor/defaultRequest");
const mime = require("rest/interceptor/mime");
const uriTemplateInterceptor = require("./api/uriTemplateInterceptor");
const errorCode = require("rest/interceptor/errorCode");
const baseRegistry = require("rest/mime/registry");
const template = require("rest/interceptor/template");
const pathPrefix = require("rest/interceptor/pathPrefix");
const csrf = require("rest/interceptor/csrf");

const registry = baseRegistry.child();

registry.register("text/uri-list", require("./api/uriListConverter"));
registry.register("application/hal+json", require("rest/mime/type/application/hal"));

let csrfToken = document.getElementsByTagName("meta")["_csrf"].getAttribute("value");
let csrfHeader = document.getElementsByTagName("meta")["_csrf_header"].getAttribute("value");

module.exports = rest
    .wrap(csrf, {token: csrfToken, name: csrfHeader})
    .wrap(pathPrefix, {prefix: "/api"})
    .wrap(template, {template: "shortURLs{?page}{&size}", page: 0, size: 10})
    .wrap(mime, { registry: registry })
    // .wrap(uriTemplateInterceptor)
    .wrap(errorCode)
    .wrap(defaultRequest, { headers: { "Accept": "application/hal+json" }});
