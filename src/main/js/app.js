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
        this.state = {shortURLs: [], attributes: [], pageSize: 5, links: []};
        this.onCreate = this.onCreate.bind(this);
        this.onDelete = this.onDelete.bind(this);
        this.onNavigate = this.onNavigate.bind(this);
        this.updatePageSize = this.updatePageSize.bind(this);
    }

    componentDidMount() {
        this.loadFromDatabase(this.state.pageSize);
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
                        onCreate={this.onCreate}
                        onUpdate={this.onUpdate}
                        attributes={this.state.attributes}
                        updatePageSize={this.updatePageSize}/>
                </Col>
            </Row>
        )
    }

    loadFromDatabase(pageSize) {
        client({params: {"size": pageSize}}).then(
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
            return client({params: {"size": this.state.pageSize}});
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
                links: shortURLCollection.entity._links
            });
        });
    }

    onDelete(shortURL) {
        client({
            method: "DELETE",
            path: shortURL._links.self.href
        }).then(response => {
            this.loadFromDatabase(this.state.pageSize);
        });
    }

    onUpdate(shortURL, updateShortURL) {
        client({
            method: "PUT",
            path: shortURL._links.self.href,
            entity: updateShortURL,
            headers: {"Content-Type": "application/json"}
        }).then(response => {
            this.loadFromDatabase(this.state.pageSize);
        })
    }

    updatePageSize(pageSize) {
        if (pageSize !== this.state.pageSize) {
            this.loadFromDatabase(pageSize);
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
        if (/^[0-9]+$/.test(pageSize)) {
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
                <ShortURL key={shortURL._links.self.href} shortURL={shortURL} onDelete={this.props.onDelete} onUpdate={this.props.onUpdate} />
        );

        let dummyShortURL = <DummyShortURL />

        let navLinks = [];
        if ("first" in this.props.links) {
            navLinks.push(<button key="first" onClick={this.handleNavFirst}>First</button>)
        }
        if ("prev" in this.props.links) {
            navLinks.push(<button key="prev" onClick={this.handleNavPrev}>Previous</button>)
        }
        if ("next" in this.props.links) {
            navLinks.push(<button key="next" onClick={this.handleNavNext}>Next</button>)
        }
        if ("last" in this.props.links) {
            navLinks.push(<button key="last" onClick={this.handleNavLast}>Last</button>)
        }

        return (
            <div>
                {/* <input ref="pageSize" defaultValue={this.props.pageSize} onInput={this.handleInput}/> */}
                <InputGroup>
                    <InputGroup.Prepend>
                        <InputGroup.Text>Maximum Table Size</InputGroup.Text>
                    </InputGroup.Prepend>
                    <FormControl ref="pageSize" defaultValue={this.props.pageSize} onInput={this.handleInput} />
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
                <div>
                    {navLinks}
                </div>
            </div>
        )
    }
}

class ShortURL extends React.Component {

    constructor(props) {
        super(props);
        this.handleDelete = this.handleDelete.bind(this);
    }

    handleDelete() {
        this.props.onDelete(this.props.shortURL);
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

        window.location = "#";
    }

    render() {

        let inputs = this.props.attributes.map(attribute =>
            // <p key={attribute}>
            //     <input type="text" placeholder={attribute} ref={attribute} className="field"/>
            // </p>
            <Form.Group key={attribute} controlId={"createForm_".concat(attribute)}>
                <Form.Label>{attribute}</Form.Label>
                <Form.Control type={attribute} placeholder={attribute} ref={attribute} />
            </Form.Group>
        );

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
