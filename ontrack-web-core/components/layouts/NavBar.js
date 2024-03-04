import {Avatar, Space, Typography} from "antd";
import UserMenu, {useUserMenu} from "@components/layouts/UserMenu";
import {useContext} from "react";
import {UserContext} from "@components/providers/UserProvider";
import {FaRegUser} from "react-icons/fa";
import HomeLink from "@components/common/HomeLink";

const {Text} = Typography;

function NavBarText({text}) {
    return (
        <Text style={{color: 'white'}}>{text}</Text>
    )
}

export default function NavBar() {

    const user = useContext(UserContext);
    const userMenu = useUserMenu();

    const openUserMenu = () => {
        userMenu.setOpen(true)
    }

    return (
        <>
            <div style={{
                display: 'flex',
                justifyContent: 'space-between',
            }}>
                <Space direction="horizontal" size={16}>
                    <HomeLink
                        text={
                            <img
                                src={`/ui/ontrack-128.png`}
                                alt="Ontrack Logo"
                                width={24}
                                height={24}
                            />
                        }
                    />
                    <HomeLink
                        text={
                            <Text
                                style={{color: "white", fontSize: '175%', verticalAlign: 'middle'}}
                            >
                                Ontrack
                            </Text>
                        }
                    />
                </Space>
                <Space direction="horizontal" size={8}>
                    {/* TODO <NavBarText text="Search component"/>*/}
                    {/* TODO <NavBarText text="App messages"/>*/}
                    <NavBarText text={user?.account?.fullName}/>
                    <Avatar icon={<FaRegUser id="user-menu"/>}
                            onClick={openUserMenu}
                            style={{
                                backgroundColor: 'white',
                                color: 'black',
                                cursor: 'pointer',
                            }}
                    />
                </Space>
            </div>

            <UserMenu userMenu={userMenu}/>
        </>
    )
}