const { post, del, sleep } = require("./common");
const cryptojs = require("crypto-js");
const assert = require("assert");

const cosmos_db_uri = process.env.COSMOS_URI; // the cosmos account URI (es. https://localhost:8081)
const databaseId = process.env.COSMOS_DATABASE;  // es. afm
const authorizationSignature = process.env.COSMOS_KEY;  // the cosmos accont Connection Primary Key (es. C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==)
const authorizationType = "master"
const authorizationVersion = "1.0";
const cosmosDBApiVersion = "2018-12-31";

async function setupTestTouchPoints(containerId, id, name, partitionKey) {
  // prior cancellation to avoid dirty cases
  await deleteTestTouchPoints(containerId, name, partitionKey);
  let responseToCheck = await createDocument(containerId, id, name, partitionKey);
  assert.strictEqual(responseToCheck.status, 201);
}

async function deleteTestTouchPoints(containerId, name, partitionKey) {
  let responseToCheck = await getDocumentByName(containerId, name);
  assert.strictEqual(responseToCheck.status, 200);
  let documents = responseToCheck.data.Documents;
  for (const element of documents) {
    await deleteDocument(containerId, element.id, partitionKey);
  }
}

async function deleteTestDataByIdPsp(containerId, idPsp, partitionKey) {
  let responseToCheck = await getDocumentByIdPsp(containerId, idPsp);
  assert.strictEqual(responseToCheck.status, 200);
  let documents = responseToCheck.data.Documents;
  for (const element of documents) {
    await deleteDocument(containerId, element.id, partitionKey);
  }
}

function getDocumentById(containerId, id) {
  const path = `dbs/${databaseId}/colls/${containerId}/docs`;
  const resourceLink = `dbs/${databaseId}/colls/${containerId}`;
  // resource type (colls, docs...)
  const resourceType = "docs";
  const date = new Date().toUTCString();
  // request method (a.k.a. verb) to build text for authorization token
  const verb = 'post';
  const authorizationToken = getCosmosDBAuthorizationToken(verb, authorizationType, authorizationVersion, authorizationSignature, resourceType, resourceLink, date);

  let partitionKeyArray = [];
  const headers = getCosmosDBAPIHeaders(authorizationToken, date, partitionKeyArray, 'application/query+json');

  const body = {
    "query": "SELECT * FROM c where c.id=@id",
    "parameters": [
      {
        "name": "@id",
        "value": id
      }
    ]
  }
  return post(cosmos_db_uri + path, body, headers)
}

function getDocumentByName(containerId, name) {
  const path = `dbs/${databaseId}/colls/${containerId}/docs`;
  const resourceLink = `dbs/${databaseId}/colls/${containerId}`;
  // resource type (colls, docs...)
  const resourceType = "docs";
  const date = new Date().toUTCString();
  // request method (a.k.a. verb) to build text for authorization token
  const verb = 'post';
  const authorizationToken = getCosmosDBAuthorizationToken(verb, authorizationType, authorizationVersion, authorizationSignature, resourceType, resourceLink, date);

  let partitionKeyArray = [];
  const headers = getCosmosDBAPIHeaders(authorizationToken, date, partitionKeyArray, 'application/query+json');

  const body = {
    "query": "SELECT * FROM c where c.name=@name",
    "parameters": [
      {
        "name": "@name",
        "value": name
      }
    ]
  }
  return post(cosmos_db_uri + path, body, headers)
}

function getDocumentByIdPsp(containerId, idPsp) {
  const path = `dbs/${databaseId}/colls/${containerId}/docs`;
  const resourceLink = `dbs/${databaseId}/colls/${containerId}`;
  // resource type (colls, docs...)
  const resourceType = "docs";
  const date = new Date().toUTCString();
  // request method (a.k.a. verb) to build text for authorization token
  const verb = 'post';
  const authorizationToken = getCosmosDBAuthorizationToken(verb, authorizationType, authorizationVersion, authorizationSignature, resourceType, resourceLink, date);

  let partitionKeyArray = [];
  const headers = getCosmosDBAPIHeaders(authorizationToken, date, partitionKeyArray, 'application/query+json');

  const body = {
    "query": "SELECT * FROM c where c.idPsp=@idPsp",
    "parameters": [
      {
        "name": "@idPsp",
        "value": idPsp
      }
    ]
  }
  return post(cosmos_db_uri + path, body, headers)
}

function createDocument(containerId, id, name, partitionKey) {
  let path = `dbs/${databaseId}/colls/${containerId}/docs`;
  let resourceLink = `dbs/${databaseId}/colls/${containerId}`;
  // resource type (colls, docs...)
  let resourceType = "docs"
  let date = new Date().toUTCString();
  // request method (a.k.a. verb) to build text for authorization token
  let verb = 'post';
  let authorizationToken = getCosmosDBAuthorizationToken(verb, authorizationType, authorizationVersion, authorizationSignature, resourceType, resourceLink, date);

  let partitionKeyArray = "[\"" + partitionKey + "\"]";
  let headers = getCosmosDBAPIHeaders(authorizationToken, date, partitionKeyArray, 'application/json');

  const body = getTouchPoint(id, name);
  return post(cosmos_db_uri + path, body, headers)
}

function deleteDocument(containerId, id, partitionKey) {
  let path = `dbs/${databaseId}/colls/${containerId}/docs/${id}`;
  let resourceLink = path;
  // resource type (colls, docs...)
  let resourceType = "docs"
  let date = new Date().toUTCString();
  // request method (a.k.a. verb) to build text for authorization token
  let verb = 'delete';
  let authorizationToken = getCosmosDBAuthorizationToken(verb, authorizationType, authorizationVersion, authorizationSignature, resourceType, resourceLink, date);

  let partitionKeyArray = "[\"" + partitionKey + "\"]";
  let headers = getCosmosDBAPIHeaders(authorizationToken, date, partitionKeyArray, 'application/json');

  return del(cosmos_db_uri + path, headers);
}

function getCosmosDBAPIHeaders(authorizationToken, date, partitionKeyArray, contentType) {

  return {
    'Accept': 'application/json',
    'Content-Type': contentType,
    'Authorization': authorizationToken,
    'x-ms-version': cosmosDBApiVersion,
    'x-ms-date': date,
    'x-ms-documentdb-isquery': 'true',
    'x-ms-documentdb-query-enablecrosspartition': 'true',
    'x-ms-documentdb-partitionkey': partitionKeyArray,
  };
}

function getCosmosDBAuthorizationToken(verb, autorizationType, autorizationVersion, authorizationSignature, resourceType, resourceLink, dateUtc) {
  // Decode authorization signature
  let key = cryptojs.enc.Base64.parse(authorizationSignature);
  // Build string to be encrypted and used as signature.
  // See: https://docs.microsoft.com/en-us/rest/api/cosmos-db/access-control-on-cosmosdb-resources
  let text = (verb || "").toLowerCase() + "\n" +
    (resourceType || "").toLowerCase() + "\n" +
    (resourceLink || "") + "\n" +
    dateUtc.toLowerCase() + "\n\n";
  // Build key to authorize request.
  let signature = cryptojs.HmacSHA256(text, key);
  // Code key as base64 to be sent.
  let signature_base64 = cryptojs.enc.Base64.stringify(signature);

  // Build autorization token, encode it and return
  return encodeURIComponent("type=" + autorizationType + "&ver=" + autorizationVersion + "&sig=" + signature_base64);
}

function getTouchPoint(id, name) {
  return { "id": id, "name": name };
}

module.exports = {
  createDocument, deleteDocument, setupTestTouchPoints, deleteTestTouchPoints, deleteTestDataByIdPsp
}