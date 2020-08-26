'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {
    
    constructor(props) {
        super(props);
        this.state = {shortURLs: []};
    }

    componentDidMount() {
        client({method: 'GET', path: '/api/shortURLs'}).then(response => {
                this.setState({shortURLs: response.entity._embedded.shortURLs});
            });
    }

    render() {
        return(<ShortURLList shortURLs = {this.state.shortURLs} />)
    }
}

class ShortURLList extends React.Component {

    render() {
        const shortURLs = this.props.shortURLs.map(shortURL =>
                <ShortURL key={shortURL._links.self.href} shortURL={shortURL}/>
        );

        return (
            <table>
                <tbody>
                    <tr>
                        <th>Name</th>
                        <th>Destination</th>
                    </tr>
                    {shortURLs}
                </tbody>
            </table>
        )
    }
}

class ShortURL extends React.Component {
    
    render() {
        return (
            <tr>
                <td>{this.props.shortURL.name}</td>
                <td>{this.props.shortURL.dest}</td>
            </tr>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)
