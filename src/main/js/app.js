"use strict";

import React from "react";
import ReactDOM from "react-dom";
import client from "./client";

import Table from "react-bootstrap/Table";
import { Button, Col, Container, Form, FormControl, InputGroup, Modal, Row } from "react-bootstrap";

const root = "/api";

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {shortURLs: [], attributes: [], pageSize: 10, currPage: 0, links: []};
        this.onCreate = this.onCreate.bind(this);
        this.onDelete = this.onDelete.bind(this);
        this.onDeleteOne = this.onDeleteOne.bind(this);
        this.onNavigate = this.onNavigate.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.updatePageSize = this.updatePageSize.bind(this);
    }

    componentDidMount() {
        this.loadFromDatabase(this.state.pageSize, this.state.currPage);
    }

    render() {
        return (
            <Row className="justify-content-center">
                <Col lg={6} className="mt-3">
                    {/* <CreateDialog attributes={this.state.attributes} onCreate={this.onCreate}/> */}
                    <ShortURLList
                        shortURLs={this.state.shortURLs}
                        links={this.state.links}
                        pageSize={this.state.pageSize}
                        onNavigate={this.onNavigate}
                        onDelete={this.onDelete}
                        onDeleteOne={this.onDeleteOne}
                        onCreate={this.onCreate}
                        onUpdate={this.onUpdate}
                        attributes={this.state.attributes}
                        updatePageSize={this.updatePageSize}/>
                </Col>
            </Row>
        )
    }

    loadFromDatabase(pageSize, currPage) {
        client({params: {"page": currPage, "size": pageSize}}).then(
            shortURLCollection => {
                return client({
                    method: "GET",
                    path: shortURLCollection.entity._links.profile.href,
                    headers: {"Accept": "application/schema+json"}
                }).then(schema => {
                    this.schema = schema.entity;
                    return shortURLCollection;
                });
            }
        ).then(shortURLCollection => {
            this.setState({
                shortURLs: shortURLCollection.entity._embedded.shortURLs,
                attributes: Object.keys(this.schema.properties),
                pageSize: pageSize,
                currPage: currPage,
                links: shortURLCollection.entity._links
            });
        });
    }

    onCreate(newShortURL) {
        client().then(shortURLCollection => {
            return client({
                method: "POST",
                path: shortURLCollection.entity._links.self.href,
                entity: newShortURL,
                headers: {"Content-Type": "application/json"}
            })
        }).then(response => {
            return client({params: {"page": this.state.currPage, "size": this.state.pageSize}});
        }).then(response => {
            if (typeof response.entity._links.last !== "undefined") {
                this.onNavigate(response.entity._links.last.href);
            } else {
                this.onNavigate(response.entity._links.self.href);
            }
        });
    }

    onNavigate(uri) {
        client({
            method:"GET",
            path: uri
        }).then(shortURLCollection => {
            this.setState({
                shortURLs: shortURLCollection.entity._embedded.shortURLs,
                attributes: this.state.attributes,
                pageSize: this.state.pageSize,
                currPage: new URL(uri).searchParams.get("page"),
                links: shortURLCollection.entity._links
            });
        });
    }

    onDelete(shortURL, alone) {
        client({
            method: "DELETE",
            path: shortURL._links.self.href
        }).then(response => {
            this.loadFromDatabase(this.state.pageSize, alone ?
                (this.state.currPage == 0 ? 0 : this.state.currPage - 1)
                : this.state.currPage);
        });
    }

    onDeleteOne(shortURL) {
        this.onDelete(shortURL, true);
    }

    onUpdate(shortURL, updateShortURL) {
        client({
            method: "PUT",
            path: shortURL._links.self.href,
            entity: updateShortURL,
            headers: {"Content-Type": "application/json"}
        }).then(response => {
            this.loadFromDatabase(this.state.pageSize, this.state.currPage);
        });
    }

    updatePageSize(pageSize) {
        if (pageSize !== this.state.pageSize) {
            this.loadFromDatabase(pageSize, 0);
        }
    }
}

class ShortURLList extends React.Component {

    constructor(props) {
        super(props);
        this.handleNavFirst = this.handleNavFirst.bind(this);
        this.handleNavLast = this.handleNavLast.bind(this);
        this.handleNavNext = this.handleNavNext.bind(this);
        this.handleNavPrev = this.handleNavPrev.bind(this);
        this.handleInput = this.handleInput.bind(this);
    }

    handleInput(e) {
        e.preventDefault();
        let pageSize = ReactDOM.findDOMNode(this.refs.pageSize).value;
        if (/^[1-9]{1}[0-9]*$/.test(pageSize)) {
            this.props.updatePageSize(pageSize);
        } else {
            ReactDOM.findDOMNode(this.refs.pageSize).value =
            pageSize.substring(0, pageSize.length - 1);
        }
    }

    handleNavFirst(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.first.href);
    }

    handleNavLast(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.last.href);
    }

    handleNavNext(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.next.href);
    }

    handleNavPrev(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.prev.href);
    }

    render() {
        let shortURLs = this.props.shortURLs.map(shortURL =>
                <ShortURL key={shortURL._links.self.href} shortURL={shortURL}
                onDelete={this.props.shortURLs.length > 1 ? this.props.onDelete : this.props.onDeleteOne}
                onUpdate={this.props.onUpdate} />
        );

        let dummyShortURL = <DummyShortURL />
        let btnLinkMap = new Map();
        btnLinkMap.set("first", [this.handleNavFirst, "First"]);
        btnLinkMap.set("prev", [this.handleNavPrev, "Previous"]);
        btnLinkMap.set("next", [this.handleNavNext, "Next"]);
        btnLinkMap.set("last", [this.handleNavLast, "Last"]);
        let navLinks = [];

        for (let [k, v] of btnLinkMap) {
            navLinks.push(<Button key={k} onClick={v[0]} disabled={!(k in this.props.links)}>{v[1]}</Button>)
        }

        return (
            <div>
                {/* <input ref="pageSize" defaultValue={this.props.pageSize} onInput={this.handleInput}/> */}
                <InputGroup>
                    <InputGroup.Prepend>
                        <InputGroup.Text>Maximum Table Size</InputGroup.Text>
                    </InputGroup.Prepend>
                    <FormControl ref="pageSize" defaultValue={this.props.pageSize} onInput={this.handleInput} />
                    <InputGroup.Append>{navLinks}</InputGroup.Append>
                </InputGroup>
                <Table striped bordered hover>
                    <thead className="thead-dark">
                        <tr>
                            <th>Name</th>
                            <th>Destination</th>
                            <th>Use Count</th>
                            {/* <th>Owner</th> */}
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <DummyShortURL attributes={this.props.attributes} onCreate={this.props.onCreate} />
                        {shortURLs}
                    </tbody>
                </Table>
            </div>
        )
    }
}

class ShortURL extends React.Component {

    constructor(props) {
        super(props);
        this.handleDelete = this.handleDelete.bind(this);
        this.copyToClipboard = this.copyToClipboard.bind(this);
    }

    handleDelete() {
        this.props.onDelete(this.props.shortURL);
    }

    copyToClipboard() {
        navigator.clipboard.writeText(window.location.host + "/to/" + this.props.shortURL["name"]);
    }

    render() {

        // let shortURLProps = Object.keys(this.props.shortURL.content);
        // // shortURLProps.pop();
        // shortURLProps = shortURLProps.map(prop =>
        //     <td key={prop}>{this.props.shortURL.content[prop]}</td>
        // )


        return (
            <tr>
                <td>{this.props.shortURL["name"]}</td>
                <td>{this.props.shortURL["destination"]}</td>
                <td>{this.props.shortURL["useCount"]}</td>
                {/* {shortURLProps} */}
                <td>
                    {/* <Button variant="warning" size="sm" shortURL={this.props.shortURL} onUpdate={this.props.onUpdate}>Edit</Button>{" "} */}
                    <Button variant="success" size="sm" onClick={this.copyToClipboard}>Copy</Button> {" "}
                    <UpdateDialog shortURL={this.props.shortURL} onUpdate={this.props.onUpdate}>Edit</UpdateDialog> {" "}
                    <Button variant="danger" size="sm" onClick={this.handleDelete}>Delete</Button>
                </td>
            </tr>
        )
    }
}

class CreateDialog extends React.Component {
    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);

        this.openDialog = this.openDialog.bind(this);
        this.closeDialog = this.closeDialog.bind(this);

        this.state = {showDialog: false};
    }

    openDialog() {
        this.setState({showDialog: true});
    }

    closeDialog() {
        this.setState({showDialog: false});
        window.location = "#";
    }

    handleSubmit(e) {
        e.preventDefault();
        let newShortURL = {};
        // this.props.attributes.forEach(attribute => {
        //     newShortURL[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim();
        // });
        newShortURL["name"] = ReactDOM.findDOMNode(this.refs["name"]).value.trim();
        newShortURL["destination"] = ReactDOM.findDOMNode(this.refs["destination"]).value.trim();

        this.props.onCreate(newShortURL);

        // this.props.attributes.forEach(attribute => {
        //     ReactDOM.findDOMNode(this.refs[attribute]).value = "";
        // });

        ReactDOM.findDOMNode(this.refs["name"]).value = "";
        ReactDOM.findDOMNode(this.refs["destination"]).value = "";

        this.closeDialog();
        window.location = "#";
    }

    render() {

        // let inputs = this.props.attributes.map(attribute =>
        //     // <p key={attribute}>
        //     //     <input type="text" placeholder={attribute} ref={attribute} className="field"/>
        //     // </p>
        //     <Form.Group key={attribute} controlId={"createForm_".concat(attribute)}>
        //         <Form.Label>{attribute}</Form.Label>
        //         <Form.Control type={attribute} placeholder={attribute} ref={attribute} />
        //     </Form.Group>
        // );

        return (
            <>
            <Button size="sm" href="#create" variant="primary" onClick={this.openDialog}>
                Create
            </Button>
            <Modal show={this.state.showDialog} onHide={this.closeDialog}>
                <Modal.Header closeButton>
                    <Modal.Title>Shorten a new URL</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group controlId="destination">
                            <Form.Label>Destination</Form.Label>
                            <Form.Control ref="destination" type="destination"></Form.Control>
                        </Form.Group>

                        <Form.Group controlId="name">
                            <Form.Label>Shortened Name</Form.Label>
                            <Form.Control ref="name" type="name" placeholder="(leave blank for autogeneration)"></Form.Control>
                        </Form.Group>
                    </Form>
                    <Button variant="primary" type="submit" onClick={this.handleSubmit}>
                        Submit
                    </Button>
                </Modal.Body>
                {/* <div id="create" className="modalDialog">
                    <div>
                        <a href="#" title="Close" className="close">X</a>
                        <h2>Create new ShortURL</h2>
                        <form>
                            {inputs}
                            <button onClick={this.handleSubmit}>Create</button>
                        </form>
                    </div>
                </div> */}
            </Modal>
            </>
        )
    }
}

class DummyShortURL extends React.Component {

    constructor(props) {
        super(props);

    }

    render() {
        return (
            <tr>
                <td>--</td>
                <td>--</td>
                <td>--</td>
                <td>
                    <CreateDialog attributes={this.props.attributes} onCreate={this.props.onCreate}/>
                </td>
            </tr>
        )
    }
}

class UpdateDialog extends React.Component {

    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.openDialog = this.openDialog.bind(this);
        this.closeDialog = this.closeDialog.bind(this);
        this.state = {
            showDialog: false
        }
    }

    openDialog() {
        this.setState({showDialog: true});
    }

    closeDialog() {
        this.setState({showDialog: false});
    }

    handleSubmit(e) {
        e.preventDefault();
        let updateShortURL = {};

        updateShortURL["name"] = ReactDOM.findDOMNode(this.refs["name"]).value.trim();
        updateShortURL["destination"] = ReactDOM.findDOMNode(this.refs["destination"]).value.trim();

        this.props.onUpdate(this.props.shortURL, updateShortURL);
        this.closeDialog();
    }

    render() {

        return (
            <>
            <Button size="sm" variant="warning" onClick={this.openDialog}>
                Edit
            </Button>
            <Modal show={this.state.showDialog} onHide={this.closeDialog}>
                <Modal.Header closeButton>
                    <Modal.Title>Edit a Shortened URL</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group controlId="destination">
                            <Form.Label>Destination</Form.Label>
                            <Form.Control ref="destination" type="destination" defaultValue={this.props.shortURL["destination"]}></Form.Control>
                        </Form.Group>

                        <Form.Group controlId="name">
                            <Form.Label>Shortened Name</Form.Label>
                            <Form.Control ref="name" type="name" defaultValue={this.props.shortURL["name"]}></Form.Control>
                        </Form.Group>
                    </Form>
                    <Button variant="primary" type="submit" onClick={this.handleSubmit}>
                        Submit
                    </Button>
                </Modal.Body>
            </Modal>
            </>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById("react")
)
