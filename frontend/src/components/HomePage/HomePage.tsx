import Logo from '@/assets/orca.svg';
import './HomePage.css';

function HomePage() {
    return (
        <div className="home-page">
            <h1> 
                Welcome to DataOrca, your personal data oracle
                <img src={Logo} className="logo" alt="SALogo"></img>
            </h1>
        </div>
    );
}

export default HomePage;